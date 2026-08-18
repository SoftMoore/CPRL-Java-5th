package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction LOAD2B.
 */
public class InstructionLOAD2B extends InstructionNoArgs
  {
    public InstructionLOAD2B(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.LOAD2B);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.LOAD2B);
      }
  }
