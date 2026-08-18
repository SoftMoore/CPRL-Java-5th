package cprlc.ast;

import common.CodeGenException;

import cprlc.Token;
import cprlc.Type;

/**
 * Base class for all initial declarations.
 */
public abstract class InitialDecl extends Declaration
  {
    /**
     * Construct an initial declaration with its identifier and type.
     */
    public InitialDecl(Token identifier, Type declType)
      {
        super(identifier, declType);
      }

    // Note: Many initial declarations do not require code generation.
    // A default implementation is provided for convenience.

    @Override
    public void emit() throws CodeGenException
      {
        // nothing to emit for most initial declarations
      }
  }
