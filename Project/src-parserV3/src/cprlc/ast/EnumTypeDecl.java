package cprlc.ast;

import cprlc.EnumType;
import cprlc.Token;

import java.util.List;

/**
 * The abstract syntax tree node for an enum type declaration.
 */
public class EnumTypeDecl extends InitialDecl
  {
    /**
     * Construct a record type declaration with its type name (identifier)
     * and list of field declarations.
     *
     * @param typeId the token containing the identifier for the enum type name
     * @param enumNames the list of tokens naming the enum constants
     */
    public EnumTypeDecl(Token typeId, List<Token> enumNames)
      {
        super(typeId, new EnumType(typeId.text(), enumNames));
      }

    @Override
    public void checkConstraints()
      {
        // nothing to do for enum types
      }
  }
