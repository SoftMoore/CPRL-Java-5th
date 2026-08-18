package cprlc.builtins;

import common.CodeGenException;
import common.Position;

import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

import cprlc.ast.FunctionDecl;

public class FunEofDecl extends FunctionDecl implements BuiltinSubprogram
  {
    private static final String name = "eof";
    private boolean isCalled = false;

    public FunEofDecl()
      {
        // create function signature: fun eof() : Boolean
        super(new Token(Symbol.identifier, Position.DEFAULT, name));
        setType(Type.Boolean);
      }

    public void setIsCalled(boolean isCalled)
      {
        this.isCalled = isCalled;
      }

    @Override
    public void checkConstraints()
      {
        // nothing to do for built-in subprograms
      }

    @Override
    public void emit() throws CodeGenException
      {
        if (isCalled)
          {
            // emit optimized assembly language instructions
            emitLabel("_eof");
            emit("LDLADDR -1");
            emit("GETEOF");
            emit("STOREB");
            emit("RET0");
          }
      }
  }
