package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.Type;

/**
 * The abstract syntax tree node for a for-loop statement.
 */
public class ForLoopStmt extends LoopStmt
  {
    private Variable   loopVar;
    private Expression rangeStart;
    private Expression rangeEnd;

    /**
     * Construct a for-loop statement with the specified
     * loop variable and range expressions.
     */
    public ForLoopStmt(Variable loopVar, Expression rangeStart, Expression rangeEnd)
      {
        this.loopVar    = loopVar;
        this.rangeStart = rangeStart;
        this.rangeEnd   = rangeEnd;
      }
    
    @Override
    public void checkConstraints()
      {
        assert loopVar != null && rangeStart != null && rangeEnd != null;

        try
          {
            loopVar.checkConstraints();
            rangeStart.checkConstraints();
            rangeEnd.checkConstraints();
            statement().checkConstraints();

            if (!rangeStart.type().isRangeType())
              {
                var errorMsg = "The first expression of a range should "
                             + "have a numeric type or an enum type.";
                throw error(rangeStart.position(), errorMsg);
              }

            if (!rangeEnd.type().isRangeType())
              {
                var errorMsg = "The second expression of a range should "
                             + "have a numeric type or an enum type.";
                throw error(rangeEnd.position(), errorMsg);
              }
            
            if (rangeStart.type() != rangeEnd.type())
              {
                var errorMsg = "The ranges of a for loop should have the same type.";
                throw error(rangeStart.position(), errorMsg);
              }

            if (rangeStart instanceof ConstValue val1 && rangeEnd instanceof ConstValue val2)
              {
                if (val1.intValue() > val2.intValue())
                  {
                    var errorMsg = "Invalid range for loop variable.";
                    throw error(val2.position(), errorMsg);
                  }
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        // initialize loop variable
        loopVar.emit();
        rangeStart.emit();
        emitStoreInst(Type.Integer);
        emitLabel(L1);

        // check that value of loop variable is <= range end
        loopVar.setUseAsExpression(true);
        loopVar.emit();
        loopVar.setUseAsExpression(false);

        rangeEnd.emit();
        emit("BG " +  L2);
        statement().emit();

        // increment loop variable
        loopVar.emit();
        loopVar.emit();
        emit("LOADW");
        emit("INC");
        emit("STOREW");

        emit("BR " + L1);
        emitLabel(L2);
      }
  }
