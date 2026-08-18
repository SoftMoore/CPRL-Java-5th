package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

/**
 * The abstract syntax tree node for a negation expression.  A negation
 * expression is a unary expression where the operand has type Integer
 * and the operator is "-".  A simple example would be "-x".
 */
public class NegationExpr extends UnaryExpr
  {
    /**
     * Construct a negation expression with the specified operator and operand.
     */
    public NegationExpr(Token operator, Expression operand)
      {
        super(operator, operand);
        setType(Type.Integer);
        assert operator.symbol() == Symbol.minus;
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
