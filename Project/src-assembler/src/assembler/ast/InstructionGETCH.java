package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction GETCH.
 */
public class InstructionGETCH extends InstructionNoArgs
  {
    public InstructionGETCH(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.GETCH);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.GETCH);
      }
  }
