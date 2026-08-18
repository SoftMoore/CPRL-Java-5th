package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction INT2CHAR.
 */
public class InstructionINT2CHAR extends InstructionNoArgs
  {
    public InstructionINT2CHAR(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.INT2CHAR);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.INT2CHAR);
      }
  }
