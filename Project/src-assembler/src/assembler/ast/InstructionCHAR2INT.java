package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction CHAR2INT.
 */
public class InstructionCHAR2INT extends InstructionNoArgs
  {
    public InstructionCHAR2INT(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.CHAR2INT);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.CHAR2INT);
      }
  }
