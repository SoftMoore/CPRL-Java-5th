package cvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Cprl
  {
    private static final String  SUFFIX = ".obj";

    // exit return value for failure
    private static final int FAILURE = -1;

    // 1K = 2**10
    private static final int K = 1024;

    // default memory size for the virtual machine
    private static final int DEFAULT_MEMORY_SIZE = 16*K;

    /**
     * This method constructs a CPRL virtual machine, loads into memory the
     * byte code from the file specified by args[0], and runs the byte code.
     *
     * @throws FileNotFoundException Thrown if the file specified in args[0] can't be found.
     */
    public static void main(String[] args) throws FileNotFoundException
      {
        if (args.length != 1)
            printUsageAndExit();

        var filename = args[0];
        var file = new File(filename);

        if (!file.isFile())
          {
            // see if we can find the file by appending the suffix
            int index = filename.lastIndexOf('.');

            if (index < 0 || !filename.substring(index).equals(SUFFIX))
              {
                filename  += SUFFIX;
                file = new File(filename);

                if (!file.isFile())
                  {
                    System.err.println("*** File " + filename + " not found ***");
                    System.exit(FAILURE);
                  }
              }
            else
              {
                // don't try to append the suffix
                System.err.println("*** File " + filename + " not found ***");
                System.exit(FAILURE);
              }
          }

        var codeFile = new FileInputStream(file);
        var cvm = new CVM(DEFAULT_MEMORY_SIZE);
        cvm.loadProgram(codeFile);
        cvm.run();
      }

    private static void printUsageAndExit()
      {
        System.err.println("Usage: cprl filename");
        System.err.println();
        System.exit(0);
      }
  }
