package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.InternalCompilerException;

import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

/**
 * The abstract syntax tree node for an adding expression.  An adding expression
 * is a binary expression where the operator is an adding operator ("+" or "-") or
 * an adding bitwise operator ("|" or "^").  A simple example would be "x + 5".
 */
public class AddingExpr extends BinaryExpr
  {
    /**
     * Construct an adding expression with the operator and the two operands.
     */
    public AddingExpr(Expression leftOperand, Token operator, Expression rightOperand)
      {
        super(leftOperand, operator, rightOperand);
        setType(Type.Integer);   // set default type for an adding expression
        assert operator.symbol().isAddingOperator();
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            leftOperand().checkConstraints();
            rightOperand().checkConstraints();

            // A plus expression is valid only for two numeric values or two strings.
            // Any other adding expression is valid only for two numeric values.

            if (operator().symbol() == Symbol.plus)
              {
                // can have two strings or two numeric types
                if (leftOperand().type().isNumericType())
                  {
                    if (!rightOperand().type().isNumericType())
                      {
                        var errorMsg ="Can't add operands of type " + leftOperand().type()
                                    + " and type " + rightOperand().type()  + ".";
                        throw error(operator().position(), errorMsg);                        
                      }
                  }
                else if (leftOperand().type() == Type.String)
                  {
                    if (rightOperand().type() != Type.String)
                      {
                        var errorMsg ="Can't add operands of type String and type "
                            + rightOperand().type()  + ".";
                        throw error(operator().position(), errorMsg);                        
                      }
                    else
                        setType(Type.String);   // change type from default to String
                  }
                else
                  {
                    var errorMsg ="Left operand should have a numeric "
                                 + "type or type String.";
                    throw error(leftOperand().position(), errorMsg);                        
                  }
              }
            else
              {
                // both operands should have a numeric type
                if (!leftOperand().type().isNumericType())
                  {
                    var errorMsg = "Left operand should have a numeric type.";
                    throw error(leftOperand().position(), errorMsg);
                  }

                if (!rightOperand().type().isNumericType())
                  {
                    var errorMsg = "Right operand should have a numeric type.";
                    throw error(rightOperand().position(), errorMsg);
                  }
              }
          }
        catch (ConstraintException ex)
          {
            errorHandler().reportError(ex);
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        leftOperand().emit();
        rightOperand().emit();

        switch (operator().symbol())
          {
            case plus       -> emit(leftOperand().type().isNumericType()
                                   ? "ADD" : "STRCAT");
            case minus      -> emit("SUB");
            case bitwiseOr  -> emit("BITOR");
            case bitwiseXor -> emit("BITXOR");
            default ->
              {
                var errorPos = operator().position();
                var errorMsg = "Invalid adding operator.";
                throw new InternalCompilerException(errorPos, errorMsg);
              }
          }
      }
  }
