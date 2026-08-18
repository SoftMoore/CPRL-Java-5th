package cprlc.builtins;

import common.CodeGenException;
import common.Position;

import cprlc.Symbol;
import cprlc.Token;
import cprlc.Type;

import cprlc.ast.ParameterDecl;
import cprlc.ast.ProcedureDecl;

import java.util.List;

public class ProcIncDecl extends ProcedureDecl implements BuiltinSubprogram
  {
    private static final String name = "inc";
    private boolean isCalled = false;

    public ProcIncDecl()
      {
        // create procedure signature: proc inc(var n : Integer)
        super(new Token(Symbol.identifier, Position.DEFAULT, name));

        var paramId    = new Token(Symbol.identifier, Position.DEFAULT, "n");
        var paramDecl  = new ParameterDecl(paramId, Type.Integer, true);
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
            emitLabel("_inc");
            emit("LDLADDR -4");
            emit("LOADW");
            emit("LDLADDR -4");
            emit("LOADW");
            emit("LOADW");
            emit("INC");
            emit("STOREW");
            emit("RET4");
          }
      }
  }
