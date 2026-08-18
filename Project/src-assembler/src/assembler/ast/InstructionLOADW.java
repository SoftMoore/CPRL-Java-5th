package assembler.ast;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction LOADW.
 */
public class InstructionLOADW extends InstructionNoArgs
  {
    public InstructionLOADW(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.LOADW);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.LOADW);
      }
  }
