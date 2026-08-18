package assembler.ast;

import assembler.Symbol;
import assembler.optimize.Optimization;
import assembler.optimize.Optimizations;

import common.ConstraintException;

import java.io.IOException;
import java.util.*;

/**
 * This class implements the abstract syntax tree for an assembly language program.
 */
public class Program extends AST
  {
    private ArrayList<Instruction> instructions;

    public Program()
      {
        super();
        instructions = new ArrayList<Instruction>(200);
      }

    public void addInstruction(Instruction inst)
      {
        instructions.add(inst);
      }

    public List<Instruction> instructions()
      {
        return instructions;
      }
    
    /*
     * Check that each instruction label is unique and check that each
     * label argument is defined for some instruction.  This method
     * should be called after parsing and before calling setAddresses().
     */
    public void checkLabels()
      {
        var labels = new HashSet<String>();
        
        // check that each instruction label is unique
        for (var inst : instructions)
          {
            for (var label : inst.labels())
              {
                try
                  {
                    var labelStr = label.text();
                    if (labels.contains(labelStr))
                      {
                        var errorMsg = "Label \"" + labelStr + "\" has already been defined.";
                        throw new ConstraintException(label.position(), errorMsg);
                      }
                    else
                        labels.add(labelStr);
                  }
                catch (ConstraintException ex)
                  {
                    errorHandler().reportError(ex);
                  }
              }
          }

        // check that label targets for all instructions are defined
        for (var inst : instructions)
          {
            if (inst instanceof InstructionOneArg instOneArg)
              {
                var arg = instOneArg.arg();

                if (arg.symbol() == Symbol.identifier)
                  {
                    String argLabel = arg.text() + ":";
                    try
                      {
                        if (!labels.contains(argLabel))
                          {
                            var errorMsg = "Label \"" + argLabel
                                         + "\" has not been defined.";
                            throw new ConstraintException(arg.position(), errorMsg);
                          }
                      }
                    catch (ConstraintException ex)
                      {
                        errorHandler().reportError(ex);
                      }
                  }
              }
          }
      }

    @Override
    public void checkConstraints()
      {
        for (var inst : instructions)
            inst.checkConstraints();
      }

    /**
     * Perform code transformations that improve performance.  This method is
     * normally called after parsing and before calling methods setAddresses(),
     * checkConstraints(), and emit().
     */
    public void optimize()
      {
        var opts = new Optimizations();
        for (int n = 0; n < instructions.size(); ++n)
          {
            for (Optimization optimization : opts.optimizations())
                optimization.optimize(instructions, n);
          }
      }

    /**
     * Sets the starting memory address for each instruction and defines label
     * addresses.  Note: This method should be called after optimizations have
     * been performed and immediately before code generation.
     */
    public void setAddresses()
      {
        // the starting address for the first instruction
        int address = 0;

        for (var inst : instructions)
          {
            try
              {
                inst.setAddress(address);
                address += inst.size();
              }
            catch (ConstraintException e)
              {
                errorHandler().reportError(e);
              }
          }
      }

    @Override
    public void emit() throws IOException
      {
        for (var inst : instructions)
            inst.emit();
      }

    @Override
    public String toString()
      {
        var buffer = new StringBuffer(1000);

        for (var inst : instructions)
            buffer.append(inst.toString())
                  .append("\n");

        return buffer.toString();
      }
  }
