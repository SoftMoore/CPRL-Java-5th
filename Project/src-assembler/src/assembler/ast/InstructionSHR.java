package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction SHR.
 */
public class InstructionSHR extends InstructionNoArgs
  {
    public InstructionSHR(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.SHR);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.SHR);
      }
  }
