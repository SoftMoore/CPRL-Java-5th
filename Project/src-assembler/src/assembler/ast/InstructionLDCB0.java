package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction LDCB0.
 */
public class InstructionLDCB0 extends InstructionNoArgs
  {
    public InstructionLDCB0(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.LDCB0);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.LDCB0);
      }
  }
