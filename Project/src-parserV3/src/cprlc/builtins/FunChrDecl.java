package cprlc.builtins;

import common.Position;
import common.CodeGenException;

import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

import cprlc.ast.FunctionDecl;
import cprlc.ast.ParameterDecl;

import java.util.List;

public class FunChrDecl extends FunctionDecl implements BuiltinSubprogram
  {
    private static final String name = "chr";
    private boolean isCalled = false;

    public FunChrDecl()
      {
        // create function signature: fun chr(n : Integer) : Char
        super(new Token(Symbol.identifier, Position.DEFAULT, name));
        setType(Type.Char);

        var paramId    = new Token(Symbol.identifier, Position.DEFAULT, "n");
        var paramDecl  = new ParameterDecl(paramId, Type.Integer, false);
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
            emitLabel("_chr");
            emit("LDLADDR -6");
            emit("LDLADDR -4");
            emit("LOADW");
            emit("INT2CHAR");
            emit("STORE2B");
            emit("RET4");
          }
      }
  }
