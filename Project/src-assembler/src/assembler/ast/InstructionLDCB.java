package assembler.ast;

import common.ConstraintException;

import assembler.Symbol;
import assembler.Token;

import cvm.Opcode;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction LDCB.
 */
public class InstructionLDCB extends InstructionOneArg
  {
    public InstructionLDCB(List<Token> labels, Token opcode, Token arg)
      {
        super(labels, opcode, arg);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.LDCB);
      }

    @Override
    public void checkArgType() throws ConstraintException
      {
        checkArgType(Symbol.intLiteral);
        var intValue = argToInt();
        if (intValue < 0 || intValue > 255)    // check unsigned byte value
          {
            var errorMsg = "Can't convert argument " + arg() + " to Byte.";
            throw error(arg().position(), errorMsg);
          }
      }
    
    private byte argToByte()
      {
        return (byte) argToInt();
      }

    @Override
    protected int argSize()
      {
        return 1;
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.LDCB);
        emit(argToByte());
      }
  }
