package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction MOD.
 */
public class InstructionMOD extends InstructionNoArgs
  {
    public InstructionMOD(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.MOD);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.MOD);
      }
  }
