package cprlc.ast;

import cprlc.ScopeLevel;
import cprlc.Type;

/**
 * Interface for a variable declaration, which can be either
 * a var declaration or a parameter declaration.
 */
public sealed interface VariableDecl permits VarDecl, ParameterDecl
  {
    /**
     * Returns the type of this declaration.
     */
    public Type type();

    /**
     * Returns the size (number of bytes) of the variable
     * declared with this declaration.
     */
    public int size();

    /**
     * Returns the scope level for this declaration.
     */
    public ScopeLevel scopeLevel();

    /**
     * Sets the relative address for a variable with this declaration.
     * Note: This method should be called before calling method relAddr().
     */
    public void setRelAddr(String idStr, int relAddr);

    /**
     * Returns the relative address (offset) of a variable
     * declared with this declaration.
     */
    public int relAddr(String idStr);
  }
