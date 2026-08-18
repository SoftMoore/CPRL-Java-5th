package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.util.IntUtil;

import cprlc.EnumType;
import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

/**
 * The abstract syntax tree node for a constant value expression, which is
 * either a literal or a declared constant identifier representing a literal.
 */
public final class ConstValue extends Expression implements Initializer
  {
    /**
     * The literal token for the constant.  If the constant value is created from a
     * constant declaration, then the literal is the one contained in the declaration.
     */
    private Token literal;

    /**
     * The constant declaration containing the constant value,  The declaration
     * is null for literals.
     */
    private ConstDecl decl;   // nonstructural reference

    /**
     * Construct a constant value from a literal token.
     */
    public ConstValue(Token literal)
      {
        super(Type.typeOf(literal), literal.position());
        this.literal = literal;
        this.decl    = null;
      }

    /**
     * Construct a constant value from a constant identifier
     * token and its corresponding constant declaration.
     */
    public ConstValue(Token identifier, ConstDecl decl)
      {
        super(decl.type(), identifier.position());
        this.literal = decl.literal();
        this.decl    = decl;
      }

    /**
     * Construct a constant value from an enum identifier
     * token and its corresponding enum type declaration.
     */
    public ConstValue(Token literal, EnumType type)
      {
        super(type, literal.position());
        this.literal = literal;
        this.decl    = null;
      }

    public Token literal()
      {
        return literal;
      }

    public void setLiteral(Token literal)
      {
        this.literal = literal;
      }

    /**
     * Returns an integer value for the declaration literal.  For an integer
     * literal, this method simply returns its integer value.  For a char
     * literal, this method returns the underlying integer value for the
     * character.  For a boolean literal, this method returns 0 for false
     * and 1 for true.  For any other literal, the method returns 0.
     */
    public int intValue()
      {
        if (literal.symbol() == Symbol.intLiteral)
          {
            try
              {
                return IntUtil.toInt(literal.text());
              }
            catch (NumberFormatException e)
              {
                // error will be reported in checkConstraints()
                return 1;
              }
          }
        else if (literal.symbol() == Symbol.trueRW)
            return 1;
        else if (literal.symbol() == Symbol.charLiteral)
          {
            char ch = literal.text().charAt(1);
            return (int) ch;
          }
        else
            return 0;
      }

    @Override
    public int size()
      {
        return type().size();
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            // Check that an integer literal can be converted to an integer.
            // Check is not required for literal in constant declaration.
            if (literal.symbol() == Symbol.intLiteral && decl == null)
              {
                try
                  {
                    IntUtil.toInt(literal.text());
                  }
                catch (NumberFormatException e)
                  {
                    var errorMsg = "The number \"" + literal.text()
                                 + "\" cannot be converted to an integer in CPRL.";
                    throw error(literal.position(), errorMsg);
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
        if (type() == Type.Integer || type() instanceof EnumType)
            emit("LDCINT " + intValue());
        else if (type() == Type.Boolean || type() == Type.Byte)
            emit("LDCB " + intValue());
        else if (type() == Type.Char)
            emit("LDCCH " + literal.text());
        else if (type() == Type.String)
            emit("LDCSTR " + literal.text());
        else
          {
            var errorMsg = "Invalid type for constant value.";
            throw new CodeGenException(literal.position(), errorMsg);
          }
      }
  }
