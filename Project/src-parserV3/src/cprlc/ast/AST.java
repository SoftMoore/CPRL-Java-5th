package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.ErrorHandler;
import common.Position;

import cprlc.IdTable;
import cprlc.Type;

import java.io.PrintWriter;

/**
 * Base class for all abstract syntax tree classes.
 */
public abstract class AST
  {
    private static PrintWriter out = new PrintWriter(System.out);

    // current label number for control flow
    private static int currentLabelNum = -1;

    private static IdTable idTable;
    private static ErrorHandler errorHandler;

    /**
     * Initializes static members that are shared with all AST subclasses.
     * The members must be re-initialized each time that the compiler is
     * run on a different file; e.g., via a command like cprlc *.cprl.
     */
    public static void reset(IdTable idTable, ErrorHandler errorHandler)
      {
        AST.idTable = idTable;
        AST.errorHandler = errorHandler;
        currentLabelNum = -1;
      }

    /**
     * Set the print writer to be used for code generation.
     */
    public static void setPrintWriter(PrintWriter out)
      {
        AST.out = out;
      }

    /**
     * Get the identifier table used during code generation.
     */
    public static IdTable idTable()
      {
        return AST.idTable;
      }

    /**
     * Get the error handler used during code generation.
     */
    public static ErrorHandler errorHandler()
      {
        return AST.errorHandler;
      }

    /**
     * Creates/returns a new constraint exception with the specified position and message.
     */
    protected ConstraintException error(Position errorPos, String errorMsg)
      {
        return new ConstraintException(errorPos, errorMsg);
      }

    /**
     * Creates/returns a new constraint exception with the specified message.
     */
    protected ConstraintException error(String errorMsg)
      {
        return new ConstraintException(errorMsg);
      }

    /**
     * Check semantic/contextual constraints.
     */
    public abstract void checkConstraints();

    /**
     * Emit object code.
     *
     * @throws CodeGenException if the method is unable to generate object code.
     */
    public abstract void emit() throws CodeGenException;

    /**
     * Returns a new value for a label.  This method should
     * be called once for each label before code generation.
     */
    protected final String newLabel()
      {
        ++currentLabelNum;
        return "L" + currentLabelNum;
      }

    /**
     * Returns true if the expression type is assignment compatible with
     * the specified type.  This method is used to compare types for
     * assignment statements, subprogram parameters, and return values.
     */
    protected boolean matchTypes(Type type, Expression expr)
      {
        // return true only if the types are equal or if
        // both types are numeric
        return type.equals(expr.type())
            ||  (type.isNumericType() && expr.type().isNumericType());
      }

    /**
     * Emits the appropriate LOAD instruction based on the type.
     */
    protected void emitLoadInst(Type t)
      {
        switch (t.size())
          {
            case 4  -> emit("LOADW");
            case 2  -> emit("LOAD2B");
            case 1  -> emit("LOADB");
            default -> emit("LOAD " + t.size());
          }
      }

    /**
     * Emits the appropriate STORE instruction based on the type.
     */
    protected void emitStoreInst(Type t)
      {
        switch (t.size())
          {
            case 4  -> emit("STOREW");
            case 2  -> emit("STORE2B");
            case 1  -> emit("STOREB");
            default -> emit("STORE " + t.size());
          }
      }

    /**
     * Emits a STORE instruction based on the number of bytes.
     */
    protected void emitStoreInst(int numBytes)
      {
        emit("STORE " + numBytes);
      }
    
    /**
     * Emit label for assembly instruction.  This instruction appends a colon
     * to the end of the label and writes out the result on a single line.
     */
    protected void emitLabel(String label)
      {
        out.println(label + ":");
      }

    /**
     * Emit string representation for an assembly instruction.
     */
    protected void emit(String instruction)
      {
        out.println("   " + instruction);
      }
  }
