package assembler.optimize;

import java.util.List;

import assembler.Symbol;
import assembler.ast.Instruction;

/**
 * Expressions involving type Byte can generate consecutive type conversion
 *   instructions BYTE2INT followed by INT2BYTE.  This optimization deletes
 *   both instructions.
 */
public class RedundantTypeConversions implements Optimization
  {
    @Override
    public void optimize(List<Instruction> instructions, int instNum)
      {
        // quick check that there are at least 2 instructions remaining
        if (instNum > instructions.size() - 2)
            return;

        var instruction0 = instructions.get(instNum);
        var instruction1 = instructions.get(instNum + 1);

        var symbol0 = instruction0.opcode().symbol();
        var symbol1 = instruction1.opcode().symbol();

        // check that we have BYTE2INT followed by INT2BYTE
        if (symbol0 == Symbol.BYTE2INT && symbol1 == Symbol.INT2BYTE)
          {
            // make sure that neither instruction has labels
            if (instruction1.labels().isEmpty() && instruction1.labels().isEmpty())
              {
                // remove both instructions
                instructions.remove(instNum);
                instructions.remove(instNum);
              }
          }
      }
  }
