package assembler.ast;

import common.ConstraintException;

import assembler.Symbol;
import assembler.Token;

import cvm.Constants;
import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction CALL.
 */
public class InstructionCALL extends InstructionOneArg
  {
    public InstructionCALL(List<Token> labels, Token opcode, Token arg)
      {
        super(labels, opcode, arg);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.CALL);
      }

    @Override
    public void checkArgType() throws ConstraintException
      {
        checkArgType(Symbol.identifier);
        checkLabelArgDefined();
      }

    @Override
    protected int argSize()
      {
        return Constants.BYTES_PER_INTEGER;
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.CALL);
        emit(getDisplacement(arg()));
      }
  }
