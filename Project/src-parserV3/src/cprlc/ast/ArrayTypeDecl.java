package cprlc.ast;

import common.ConstraintException;

import cprlc.ArrayType;
import cprlc.Token;
import cprlc.Type;

/**
 * The abstract syntax tree node for an array type declaration.
 */
public class ArrayTypeDecl extends InitialDecl
  {
    private ConstValue numElements;

    /**
     * Construct an array type declaration with its identifier, element type, and
     * number of elements.  Note that the index type is always Integer in CPRL.
     *
     * @param typeId      The token containing the identifier for the array.
     * @param elementType The type of elements in the array.
     * @param numElements The number of elements in the array.
     */
    public ArrayTypeDecl(Token typeId, Type elemType, ConstValue numElements)
      {
        super(typeId, new ArrayType(typeId.text(), numElements.intValue(), elemType));
        this.numElements = numElements;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }
  }
