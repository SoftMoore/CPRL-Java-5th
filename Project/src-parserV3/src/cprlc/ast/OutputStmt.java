package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.EnumType;
import cprlc.Type;

import java.util.List;

/**
 * This class implements the abstract syntax tree for both write and writeln statements.
 */
public class OutputStmt extends Statement
  {
    private List<Expression> expressions;
    private boolean isWriteln;

    /**
     * Construct an output statement with the list of expressions and isWriteln flag.
     */
    public OutputStmt(List<Expression> expressions, boolean isWriteln)
      {
        this.expressions = expressions;
        this.isWriteln   = isWriteln;
      }

    /**
     * Construct an output statement with the list of expressions.
     * The isWriteln flag is initialized to false.
     */
    public OutputStmt(List<Expression> expressions)
      {
        this(expressions, false);
      }

    /**
     * Returns the list of expressions for this output statement.
     */
    public List<Expression> expressions()
      {
        return expressions;
      }
    
    @Override
    public void checkConstraints()
      {
        for (var expr : expressions)
          {
            expr.checkConstraints();

            try
              {
                if (!expr.type().isOutputType())
                  {
                    var errorMsg = "Output supported only for scalar types and strings.";
                    throw error(expr.position(), errorMsg);
                  }
              }
            catch (ConstraintException e)
              {
                errorHandler().reportError(e);
              }
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        for (var expr : expressions)
          {
            var type = expr.type();

            expr.emit();

            if (type == Type.Byte || type == Type.Boolean)
                emit("BYTE2INT");

            if (type.isNumericType() || type == Type.Boolean || type instanceof EnumType)
                emit("PUTINT");
            else if (type == Type.Char)
                emit("PUTCH");
            else if (type == Type.String)   // must be a string type
                emit("PUTSTR");
            else
              {
                var errorMsg = "Unexpected type in output statement";
                throw new CodeGenException(expr.position(), errorMsg);
              }
          }

        if (isWriteln)
            emit("PUTEOL");
      }
  }
