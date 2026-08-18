package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.Position;

import cprlc.Type;

/**
 * The abstract syntax tree node for an assignment statement.
 */
public class AssignmentStmt extends Statement
  {
    private Variable   variable;
    private Expression expr;

    // position of the assignment symbol (for error reporting)
    private Position assignPosition;

    /**
     * Construct an assignment statement with a variable, an expression,
     * and the position of the assignment symbol.
     *
     * @param variable The variable on the left side of the assignment symbol.
     * @param expr     The expression on the right side of the assignment symbol.
     * @param assignPosition Position of the assignment symbol (for error reporting).
     */
    public AssignmentStmt(Variable variable, Expression expr, Position assignPosition)
      {
        this.variable = variable;
        this.expr = expr;
        this.assignPosition = assignPosition;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
// ...
      }
  }
