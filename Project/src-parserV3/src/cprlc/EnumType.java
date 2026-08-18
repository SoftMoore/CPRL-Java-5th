package cprlc;

import common.InternalCompilerException;

import cvm.Constants;

import java.util.HashMap;
import java.util.List;


/**
 * This class encapsulates the language concept of an enum type
 * in the programming language CPRL.
 */
public class EnumType extends Type
  {
    private List<Token> enumConstants;

    // Use a hash map for efficient lookup of ord values for enum names.
    private HashMap<String, Integer> nameValueMap = new HashMap<>();

    /**
     * Construct an enum type with the specified type name
     * and list of enum constants.
     */
    public EnumType(String typeName, List<Token> enumConstants)
      {
        super(typeName, 0);
// ... In call to superclass constructor, 0 is not correct as the size for the
// ... enum type.  What is the size for the string type?  Hint: Read the book.

        this.enumConstants = enumConstants;

// ...  Update nameValueMap for each enum constant in the list
// ...  Hint: Use a for loop similar to the following.
// ...  for (int i = 0; i < enumConstants.size(); ++i)
// ...
      }

    /**
     * Returns true if this enum type contains a name with the specified
     * identifier string.
     */
    public boolean containsName(String idStr)
      {
        return nameValueMap.containsKey(idStr);
      }

    /**
     * Returns the number of enum constants declared in this enum type.
     */
    public int numEnumConstants()
      {
        return 0;
// ...  return 0; is not correct.  How many enum constants are there?
// ...  Hint: use the list of constants
      }

    /**
     * Returns the numeric value associated with the enum identifier
     * string.  Returns -1 if the identifier string is not found.
     */
    public int getValue(String idStr)
      {
        return containsName(idStr) ? nameValueMap.get(idStr) : -1;
      }

    /**
     * Returns the enum constant token associated with the specified integer value.
     *
     * @throws InternalCompilerException if ord is out of range for this enum type.
     */
    public Token getName(int ord)
      {
        if (ord >= 0 && ord < enumConstants.size())
            return enumConstants.get(ord);
        else
          {
            var errorMsg = "Ord value out of range for enum type " + this + ": " + ord;
            throw new InternalCompilerException(errorMsg);
          }
      }
  }
