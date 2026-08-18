package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.ArrayType;
import cprlc.Token;

import cprlc.builtins.BuiltinSubprogram;

import java.util.List;

/**
 * The abstract syntax tree node for a function call expression.
 */
public class FunctionCallExpr extends Expression
  {
    private Token funId;
    private List<Expression> actualParams;

    // declaration of the function being called
    private FunctionDecl funDecl;   // nonstructural reference

    /**
     * Construct a function call expression with the function name (an identifier
     * token) and the list of actual parameters being passed as part of the call.
     */
    public FunctionCallExpr(Token funId, List<Expression> actualParams)
      {
        super(funId.position());
        this.funId = funId;
        this.actualParams = actualParams;
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            // get the declaration for this function call from the identifier table
            var decl = idTable().get(funId.text());

            if (decl == null)
              {
                var errorMsg = "Function \"" + funId.text() + "\" has not been declared.";
                throw error(funId.position(), errorMsg);
              }
            
            if (!(decl instanceof FunctionDecl))
              {
                var errorMsg = "Identifier \"" + funId.text()
                             + "\" was not declared as a function.";
                throw error(funId.position(), errorMsg);
              }
            else
                funDecl = (FunctionDecl) decl;

            // At this point funDecl should not be null.
            if (funDecl instanceof BuiltinSubprogram biFunDecl)
                biFunDecl.setIsCalled(true);

            setType(funDecl.type());

            var paramDecls = funDecl.parameterDecls();

            // check that numbers of parameters match
            if (actualParams.size() != paramDecls.size())
              {
                var errorMsg = "Incorrect number of actual parameters.";
                throw error(funId.position(), errorMsg);
              }

            // check constraints for each actual parameter
            for (var expr : actualParams)
                expr.checkConstraints();

            for (int i = 0; i < actualParams.size(); ++i)
              {
                var actualParam = actualParams.get(i);
                var paramDecl   = paramDecls.get(i);

                // check that types are equal
                if (!matchTypes(paramDecl.type(), actualParam))
                  {
                    var errorMsg = "Parameter type mismatch.";
                    throw error(actualParam.position(), errorMsg);
                  }

                // check that arrays are passed as var parameters
                if (paramDecl.type() instanceof ArrayType)
                  {
                    if (actualParam instanceof Variable variable)
                        variable.setUseAsExpression(false);
                    else
                      {
                        var errorMsg = "Expression for an array parameter must be a variable.";
                        throw error(actualParam.position(), errorMsg);
                      }
                  }
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        // allocate space on the stack for the return value
        emit("ALLOC " + funDecl.type().size());

        // emit code for actual parameters
        for (var expr : actualParams)
            expr.emit();

        emit("CALL " + funDecl.subprogramLabel());
      }
  }
