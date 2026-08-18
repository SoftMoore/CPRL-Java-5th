package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.ArrayType;
import cprlc.Token;

import cprlc.builtins.BuiltinSubprogram;

import java.util.List;

/**
 * The abstract syntax tree node for a procedure call statement.
 */
public class ProcedureCallStmt extends Statement
  {
    private Token procId;
    private List<Expression> actualParams;

    // declaration of the procedure being called
    private ProcedureDecl procDecl;   // nonstructural reference

    /*
     * Construct a procedure call statement with the procedure name
     * (an identifier token) and the list of actual parameters being
     * passed as part of the call.
     */
    public ProcedureCallStmt(Token procId, List<Expression> actualParams)
      {
        this.procId = procId;
        this.actualParams = actualParams;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
// ...
      }
  }
