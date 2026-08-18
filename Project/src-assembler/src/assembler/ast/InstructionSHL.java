package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction SHL.
 */
public class InstructionSHL extends InstructionNoArgs
  {
    public InstructionSHL(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.SHL);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.SHL);
      }
  }
