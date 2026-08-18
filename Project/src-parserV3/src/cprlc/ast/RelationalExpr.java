package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import cprlc.EnumType;
import cprlc.Token;
import cprlc.Type;

/**
 * The abstract syntax tree node for a relational expression.  A relational
 * expression is a binary expression where the operator is a relational
 * operator such as "&lt;=" or "&gt;".  A simple example would be "x &lt; 5".
 */
public class RelationalExpr extends BinaryExpr
  {
    // labels used during code generation
    private String L1 = newLabel();   // label at start of right operand
    private String L2 = newLabel();   // label at end of the relational expression

    /**
     * Construct a relational expression with the operator ("=", "&lt;=", etc.)
     * and the two operands.
     */
    public RelationalExpr(Expression leftOperand, Token operator, Expression rightOperand)
      {
        super(leftOperand, operator, rightOperand);
        setType(Type.Boolean);
        assert operator.symbol().isRelationalOperator();
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
        emitBranch(false, L1);
        emit("LDCB " + TRUE);    // push true back on the stack
        emit("BR " + L2);        // jump over code to emit false
        emitLabel(L1);
        emit("LDCB " + FALSE);   // push false onto the stack
        emitLabel(L2);
      }

    @Override
    public void emitBranch(boolean condition, String label) throws CodeGenException
      {
        emitOperand(leftOperand());
        emitOperand(rightOperand());

        switch (operator().symbol())
          {
            case equals         -> emit(condition ? "BE "  + label : "BNE " + label);
            case notEqual       -> emit(condition ? "BNE " + label : "BE "  + label);
            case lessThan       -> emit(condition ? "BL "  + label : "BGE " + label);
            case lessOrEqual    -> emit(condition ? "BLE " + label : "BG "  + label);
            case greaterThan    -> emit(condition ? "BG "  + label : "BLE " + label);
            case greaterOrEqual -> emit(condition ? "BGE " + label : "BL "  + label);
            default ->
              {
                var position = operator().position();
                var errorMsg = "Invalid relational operator.";
                throw new CodeGenException(position, errorMsg);
              }
          }
      }

    private void emitOperand(Expression operand) throws CodeGenException
      {
        // Relational operators compare only integers, so we need to make
        // sure that we have enough bytes on the stack for each operand.
        if (operand.type().isNumericType() || operand.type() instanceof EnumType)
            operand.emit();
        else if (operand.type() == Type.Boolean)
          {
            operand.emit();
            emit("BYTE2INT");
          }
        else if (operand.type() == Type.Char)
          {
            operand.emit();
            emit("CHAR2INT");
          }
      }
  }
