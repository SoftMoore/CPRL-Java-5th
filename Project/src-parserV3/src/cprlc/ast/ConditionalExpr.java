package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.Position;

import cprlc.Type;

public class ConditionalExpr extends Expression
  {
    private Expression condition;
    private Expression exprTrue;
    private Expression exprFalse;

    // labels used during code generation
    private String L1 = newLabel();   // label of address at end of exprTrue
    private String L2 = newLabel();   // label of address at end of exprFalse

    /**
     * Construct an assignment statement with a variable, an expression,
     * and the position of the assignment symbol.
     *
     * @param condition The boolean expression on the left side of the question mark
     *                  symbol. If condition is true, then exprTrue is evaluated,
     *                  and its result is the result of the compound expression.
     *                  Otherwise exprFalse is evaluated, and its result is the
     *                  result of the compound expression.
     * @param exprTrue  The expression whose value is used if the condition is true.
     * @param exprFalse The expression whose value is used if the condition is false.
     * @param colonPosition Position of the colon symbol (for error reporting).
     */
    public ConditionalExpr(Expression condition,
                           Expression exprTrue,
                           Expression exprFalse,
                           Position   colonPosition)
      {
        super(exprTrue.type(), condition.position());
        this.condition = condition;
        this.exprTrue  = exprTrue;
        this.exprFalse = exprFalse;
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            condition.checkConstraints();
            exprTrue.checkConstraints();
            exprFalse.checkConstraints();

            if (condition.type() != Type.Boolean)
              {
                var errorMsg = "The first expression for a conditional"
                             + " expression should have type Boolean";
                throw error(condition.position(), errorMsg);
              }

            if (!exprTrue.type().equals(exprFalse.type()))
              {
                setType(Type.UNKNOWN);
                var errorMsg = "For a conditional expression, the expressions on the "
                             + "left and right of the colon should have the same type.";
                throw error(position(), errorMsg);
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
        condition.emitBranch(false, L1);  // branch to L1 if false
        exprTrue.emit();
        emit("BR " + L2);                 // branch to L2
        emitLabel(L1);                    // L1:
        exprFalse.emit();
        emitLabel(L2);                    // L2:
      }
  }
