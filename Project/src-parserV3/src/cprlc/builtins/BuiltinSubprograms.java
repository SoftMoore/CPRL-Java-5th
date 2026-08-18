package cprlc.builtins;

import cprlc.ast.FunctionDecl;
import cprlc.ast.ProcedureDecl;
import cprlc.ast.SubprogramDecl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides access to all built-in subprograms.
 */
public class BuiltinSubprograms
  {
    // map built-in subprogram names to their declarations
    private Map<String, SubprogramDecl> nameDeclMap = new HashMap<>();
    
    public BuiltinSubprograms()
      {
        // add built-in function declarations
        var chrDecl = new FunChrDecl();
        nameDeclMap.put(chrDecl.idToken().text(), chrDecl);

        var eofDecl = new FunEofDecl();
        nameDeclMap.put(eofDecl.idToken().text(), eofDecl);

        var ordDecl = new FunOrdDecl();
        nameDeclMap.put(ordDecl.idToken().text(), ordDecl);
        
        // add built-in procedure declarations
        var incDecl = new ProcIncDecl();
        nameDeclMap.put(incDecl.idToken().text(), incDecl);
      }

    /**
     * Returns true if the specified name is the function name for
     * a built-in function declaration.
     */
    public boolean isFunctionDeclName(String name)
      {
        return nameDeclMap.keySet().contains(name)
            && (nameDeclMap.get(name) instanceof FunctionDecl);
      }

    /**
     * Returns true if the specified name is the procedure name for a
     * built-in procedure declaration.
     */
    public boolean isProcedureDeclName(String name)
      {
        return nameDeclMap.keySet().contains(name)
            && nameDeclMap.get(name) instanceof ProcedureDecl;
      }

    /**
     * Returns the built-in subprogram declaration with the specified name.
     * Returns null if there is no built-in subprogram declaration with that name.
     */
    public SubprogramDecl decl(String name)
      {
        return nameDeclMap.get(name);  
      }

    /**
     * Returns the collection of all built-in subprogram declarations.
     */
    public Collection<SubprogramDecl> subprogramDecls()
      {
        return nameDeclMap.values();
      }
  }
