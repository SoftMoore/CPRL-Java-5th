package cprlc;

import cvm.Constants;

/**
 * This class encapsulates the language types for the programming language CPRL.
 * Type sizes are initialized to values appropriate for the CPRL virtual machine.
 */
public class Type
  {
    private String typeName;
    private int size;

    // predefined types
    public static final Type Boolean = new Type("Boolean", Constants.BYTES_PER_BOOLEAN);
    public static final Type Byte    = new Type("Byte",    1);
    public static final Type Char    = new Type("Char",    Constants.BYTES_PER_CHAR);
    public static final Type Integer = new Type("Integer", Constants.BYTES_PER_INTEGER);
    public static final Type String  = new Type("String",  Constants.BYTES_PER_ADDRESS);

    // an address of the target machine
    public static final Type Address = new Type("Address", Constants.BYTES_PER_ADDRESS);

    // compiler-internal types
    public static final Type UNKNOWN = new Type("UNKNOWN");
    public static final Type none    = new Type("none");

    /**
     * Construct a new type with the specified type name and size.
     */
    protected Type(String typeName, int size)
      {
        this.typeName = typeName;
        this.size = size;
      }

    /**
     * Construct a new type with the specified type name.
     * Size is initially set to 0.
     */
    protected Type(String typeName)
      {
        this(typeName, 0);
      }

    /**
     * Returns the number of machine addressable units
     * (e.g., bytes or words) for this type.
     */
    public int size()
      {
        return size;
      }

    /**
     * Returns true if and only if this type is a scalar type.  The scalar
     * types in CPRL are Integer, Boolean, Byte, Char, and enum types.
     */
    public boolean isScalarType()
      {
        return this == Integer || this == Boolean
            || this == Byte    || this == Char
            || this instanceof EnumType;
      }

    /**
     * Returns true if and only if this type is a composite type.
     * The composite types in CPRL are array types and record types.
     */
    public boolean isCompositeType()
      {
        return !isScalarType();
      }

    /**
     * Returns true if and only if this type is a numeric type.
     * The numeric types in CPRL are Integer and Byte.
     */
    public boolean isNumericType()
      {
        return this == Integer || this == Byte;
      }

    /**
     * Returns true if and only if a value of this type can be used
     * as an index for an array or string.  The index types in CPRL
     * are Integer, Byte, and enum types.
     */
    public boolean isIndexType()
      {
        return this.isNumericType() || this instanceof EnumType;
      }

    /**
     * Returns true if and only if a value of this type can be used as
     * a range in a for loop.  The range types in CPRL are exactly the
     * same as the index types; i.e. Integer, Byte, and enum types.
     */
    public boolean isRangeType()
      {
        return this.isNumericType() || this instanceof EnumType;
      }

    /**
     * Returns true if and only if a value of this type can be used
     * in a read statement.  The valid input types in CPRL are
     * numeric types, Char, and String.
     */
    public boolean isInputType()
      {
        return this.isNumericType() || this == Char || this == String;
      }

    /**
     * Returns true if and only if a value of this type can be used
     * in a write or writeln statement.  The valid output types in
     * CPRL are scalar types and String.
     */
    public boolean isOutputType()
      {
        return this.isScalarType() || this == String;
      }

    /**
     * Returns the type of a literal symbol.  For example, if the
     * symbol is an intLiteral, then Type.Integer is returned.
     * Returns UNKNOWN if the symbol is not a valid literal symbol.
     */
    public static Type typeOf(Token literal)
      {
        return switch (literal.symbol())
          {
            case Symbol.intLiteral    -> Integer;
            case Symbol.charLiteral   -> Char;
            case Symbol.trueRW        -> Boolean;
            case Symbol.falseRW       -> Boolean;
            case Symbol.stringLiteral -> String;
            default -> Type.UNKNOWN;
          };
      }

    /**
     * Returns the name for this type.
     */
    @Override
    public String toString()
      {
        return typeName;
      }

    @Override
    public int hashCode()
      {
        return typeName.hashCode();
      }

    @Override
    public boolean equals(Object other)
      {
        return (this == other)
            || (other instanceof Type otherType
            && typeName.equals(otherType.typeName));
      }
  }
