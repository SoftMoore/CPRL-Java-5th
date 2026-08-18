package cprlc;

import common.CodeGenException;
import common.ErrorHandler;
import common.FatalException;
import common.ParserException;

import cprlc.ast.AST;
import cprlc.builtins.BuiltinSubprograms;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Compiler for CPRL.
 */
public class Cprlc
  {
    private static final String SUFFIX = ".cprl";

    /**
     * This method drives the compilation process.
     *
     * @param args Must include the name of the CPRL source file, either the complete
     *             file name or the base file name with suffix ".cprl" omitted.
     */
    public static void main(String[] args) throws Exception
      {
        if (args.length == 0)
            printUsageAndExit();

        for (var fileName : args)
          {
            try
              {
                var sourceFile = new File(fileName);

                if (!sourceFile.isFile())
                  {
                    // see if we can find the file by appending the suffix
                    int index = fileName.lastIndexOf('.');

                    if (index < 0 || !fileName.substring(index).equals(SUFFIX))
                      {
                        fileName  += SUFFIX;
                        sourceFile = new File(fileName);

                        if (!sourceFile.isFile())
                            throw new FatalException("File \"" + fileName + "\" not found");
                      }
                    else
                      {
                        // don't try to append the suffix
                        throw new FatalException("File \"" + fileName + "\" not found");
                      }
                  }

                var compiler = new Cprlc();
                compiler.compile(sourceFile);
              }
            catch (FatalException e)
              {
                // report error and continue compiling
                var errorHandler = new ErrorHandler();
                errorHandler.reportFatalError(e);
              }

            System.out.println();
          }
      }

    /**
     * Compiles the source file.  If there are no errors in the source file,
     * the object code is placed in a file with the same base file name as
     * the source file but with a ".asm" suffix.
     *
     * @throws IOException Thrown if there are problems reading the source
     *                     file or writing to the target file.
     */
    public void compile(File sourceFile) throws IOException, ParserException
      {
        var errorHandler = new ErrorHandler();
        var scanner = new Scanner(sourceFile, 4, errorHandler);   // 4 lookahead tokens
        var idTable = new IdTable();

        // Add built-in subprogram declarations to the identifier table at
        // global scope and open a new global scope for the CPRL program.
        var builtins = new BuiltinSubprograms();
        for (var builtinDecl : builtins.subprogramDecls())
            idTable.add(builtinDecl.idToken(), builtinDecl);
        idTable.openScope(ScopeLevel.GLOBAL);

        var parser = new Parser(scanner, idTable, errorHandler);
        AST.reset(idTable, errorHandler);

        try (scanner)
          {
            printProgressMessage("Starting compilation for " + sourceFile.getName());
            printProgressMessage("...parsing");

            // parse source file
            var program = parser.parseProgram();

            // check constraints
            if (!errorHandler.errorsExist())
              {
                printProgressMessage("...checking constraints");
                program.checkConstraints();
              }

            // generate code
            if (!errorHandler.errorsExist())
              {
                printProgressMessage("...generating code");

                // no error recovery from errors detected during code generation
                var printWriter = targetPrintWriter(sourceFile);
                try (printWriter)
                  {
                    AST.setPrintWriter(printWriter);
                    program.emit();
                    
                    // will emit built-in subprograms only if they were called
                    for (var builtinDecl : builtins.subprogramDecls())
                        builtinDecl.emit();
                  }
                catch (CodeGenException ex)
                  {
                    errorHandler.reportError(ex);
                  }
              }

            if (errorHandler.errorsExist())
                errorHandler.printMessage("Errors detected in " + sourceFile.getName()
                                        + " -- compilation terminated.");
            else
                printProgressMessage("Compilation complete.");
          }
      }

    /**
     * Returns a print writer used for writing the assembly code.  The target
     * print writer writes to a file with the same base file name as the source
     * file but with a ".asm" suffix.
     */
    private PrintWriter targetPrintWriter(File sourceFile)
      {
        // get source file name minus the suffix
        var baseName = sourceFile.getName();
        int suffixIndex = baseName.lastIndexOf(SUFFIX);
        if (suffixIndex > 0)
            baseName = baseName.substring(0, suffixIndex);

        var targetFileName = baseName + ".asm";

        try
          {
            var targetFile = new File(sourceFile.getParent(), targetFileName);
            var fileWriter = new FileWriter(targetFile, StandardCharsets.UTF_8);
            return new PrintWriter(fileWriter, true);
          }
        catch (IOException e)
          {
            e.printStackTrace();
            throw new FatalException("Failed to create file " + targetFileName);
          }
      }

    private void printProgressMessage(String message)
      {
        System.out.println(message);
      }

    private static void printUsageAndExit()
      {
        System.err.println("Usage: cprlc file1 file2 ...");
        System.err.println();
        System.exit(0);
      }
  }
