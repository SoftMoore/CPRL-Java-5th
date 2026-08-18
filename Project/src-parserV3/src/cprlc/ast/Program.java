package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import java.util.Collections;
import java.util.List;

/**
 * The abstract syntax tree node for a CPRL program.
 */
public class Program extends AST
  {
    private List<InitialDecl>    initialDecls;
    private List<SubprogramDecl> subprogramDecls;

    private int varLength;   // # bytes of all declared variables

    /**
     * Construct a program with the specified initial declarations
     * and subprogram declarations.
     */
    public Program(List<InitialDecl> initialDecls, List<SubprogramDecl> subprogramDecls)
      {
        this.initialDecls    = initialDecls;
        this.subprogramDecls = subprogramDecls;
        this.varLength       = 0;
      }

    /**
     * Construct a program with empty lists of initial and subprogram declarations.
     */
    public Program()
      {
        this(Collections.emptyList(), Collections.emptyList());
      }

    public void addSubprogramDecl(SubprogramDecl decl)
      {
        subprogramDecls.add(decl);
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            for (var decl : initialDecls)
                decl.checkConstraints();

            for (var decl : subprogramDecls)
                decl.checkConstraints();

            // check for procedure main
            var decl = idTable().get("main");
            if (decl == null)
                throw error("Program is missing procedure \"main()\".");
            else if (!(decl instanceof ProcedureDecl))
              {
                var errorMsg = "Identifier \"main\" was not declared as a procedure.";
                throw error(decl.position(), errorMsg);
              }
            else
              {
                ProcedureDecl procDecl = (ProcedureDecl) decl;
                if (procDecl.paramLength() != 0)
                  {
                    var errorMsg = "Procedure \"main\" cannot have parameters.";
                    throw error(decl.position(), errorMsg);
                  }
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    /**
     * Set the relative address (offset) for each variable declaration
     * and compute the length of all variables.
     */
    private void setRelativeAddresses()
      {
        // initial relative address is 0 for a program
        int currentAddr = 0;

        for (var decl : initialDecls)
          {
            if (decl instanceof VarDecl varDecl)
              {
                // set relative address for each identifier
                for (var idName : varDecl.idNames())
                  {
                    varDecl.setRelAddr(idName, currentAddr);
                    currentAddr = currentAddr + varDecl.size();
                  }
              }
          }

        // compute length of all variables
        varLength = currentAddr;
      }

    @Override
    public void emit() throws CodeGenException
      {
        setRelativeAddresses();

        // no need to emit PROGRAM instruction if varLength == 0
        if (varLength > 0)
            emit("PROGRAM " + varLength);

        for (var decl : initialDecls)
            decl.emit();

        emit("CALL _main");
        emit("HALT");

        for (var decl : subprogramDecls)
            decl.emit();
      }
  }
