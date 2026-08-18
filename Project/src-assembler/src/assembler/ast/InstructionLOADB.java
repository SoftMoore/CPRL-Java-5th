package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction LOADB.
 */
public class InstructionLOADB extends InstructionNoArgs
  {
    public InstructionLOADB(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.LOADB);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.LOADB);
      }
  }
