package cprlc;

import common.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * The identifier table (also known as a symbol table) is used to
 * hold attributes of identifiers in the programming language CPRL.
 */
public final class IdTable
  {
    // IdTable is implemented as a stack of scopes. Opening a scope
    // pushes a new scope onto the stack. Searching for an identifier
    // involves searching at the current level (top scope in the stack)
    // and then at enclosing scopes (scopes under the top).

    private static final int INITIAL_SCOPES = 3;

    private List<Scope> table = new ArrayList<>(INITIAL_SCOPES);

    // Index of the top scope.  The top index is incremented every time a
    // new scope is opened and decremented every time a scope is closed.
    private int topIndex = 0;

    /**
     * Construct an empty identifier table with scope level initialized to GLOBAL.
     */
    public IdTable()
      {
        table.add(topIndex, new Scope(ScopeLevel.GLOBAL));
      }

    /**
     * Returns the current scope level.
     */
    public ScopeLevel scopeLevel()
      {
        return table.get(topIndex).scopeLevel();
      }

    /**
     * Opens a new scope for identifiers.
     */
    public void openScope(ScopeLevel scopeLevel)
      {
        ++topIndex;
        table.add(topIndex, new Scope(scopeLevel));
      }

    /**
     * Closes the outermost scope.
     */
    public void closeScope()
      {
        table.remove(topIndex);
        --topIndex;
      }

    /**
     * Add an identifier token and its type to the current scope.
     *
     * @throws ParserException if the identifier already exists
     *         in the current scope.
     */
    public void add(Token idToken, IdType idType) throws ParserException
      {
        // assumes that idToken is an identifier token
        assert idToken.symbol() == Symbol.identifier :
            "IdTable.add(): The token is not an identifier.";

        var scope   = table.get(topIndex);
        var oldDecl = scope.put(idToken.text(), idType);

        // check that the identifier has not been defined previously
        if (oldDecl != null)
          {
            var errorMsg = "Identifier \"" + idToken.text()
                         + "\" is already defined in the current scope.";
            throw new ParserException(idToken.position(), errorMsg);
          }
      }

    /**
     * Returns the identifier type associated with the identifier name
     * (type String).  Returns null if the identifier is not found.
     * Searches enclosing scopes if necessary.
     */
    public IdType get(String idStr)
      {
        IdType idType = null;
        int    index  = topIndex;

        while (index >= 0 && idType == null)
          {
            var scope = table.get(index);
            idType = scope.get(idStr);
            --index;
          }

        return idType;
      }
  }
