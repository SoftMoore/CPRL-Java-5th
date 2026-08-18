package cprlc.builtins;

import common.CodeGenException;
import common.Position;

import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

import cprlc.ast.FunctionDecl;
import cprlc.ast.ParameterDecl;

import java.util.List;

public class FunOrdDecl extends FunctionDecl implements BuiltinSubprogram
  {
    private static final String name = "ord";
    private boolean isCalled = false;

    public FunOrdDecl()
      {
        // create function signature: fun ord(c : Char) : Integer
        super(new Token(Symbol.identifier, Position.DEFAULT, name));
        setType(Type.Integer);

        var paramId    = new Token(Symbol.identifier, Position.DEFAULT, "c");
        var paramDecl  = new ParameterDecl(paramId, Type.Char, false);
        var paramDecls = List.of(paramDecl);
        setParameterDecls(paramDecls);
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
            emitLabel("_ord");
            emit("LDLADDR -6");
            emit("LDLADDR -2");
            emit("LOAD2B");
            emit("CHAR2INT");
            emit("STOREW");
            emit("RET 2");
          }
      }
  }
