package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.InternalCompilerException;

import cprlc.Token;
import cprlc.Type;

/**
 * The abstract syntax tree node for a multiplying expression.  A multiplying
 * expression is a binary expression where the operator is a multiplying
 * operator such a "*", "/", "mod", "<<",  etc.  A simple example would be "5*x".
 */
public class MultiplyingExpr extends BinaryExpr
  {
    /**
     * Construct a multiplying expression with the operator and the two operands.
     */
    public MultiplyingExpr(Expression leftOperand, Token operator, Expression rightOperand)
      {
        super(leftOperand, operator, rightOperand);
        setType(Type.Integer);
        assert operator.symbol().isMultiplyingOperator();
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
