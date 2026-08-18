package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction INC.
 */
public class InstructionINC extends InstructionNoArgs
  {
    public InstructionINC(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.INC);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.INC);
      }
  }
