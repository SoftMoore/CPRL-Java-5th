package cvm;

import common.util.ByteUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * This class implements a virtual machine for the programming language
 * CPRL.  It interprets instructions for a hypothetical CPRL computer.
 */
public class CVM
  {
    private static final boolean DEBUG = false;

    // virtual machine constant for false
    private static final byte FALSE = (byte) 0;

    // virtual machine constant for true
    private static final byte TRUE = (byte) 1;

    // end of file
    private static final int EOF = -1;

    // scanner for handling integer and string input
    private Scanner scanner = new Scanner(System.in);

    // Reader for handling char input
    private Reader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);

    // PrintStream for handling output
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    // computer memory (for the virtual CPRL machine)
    private byte[] memory;

    // program counter (address of the next instruction in memory)
    private int pc = 0;

    // base pointer
    private int bp = 0;

    // stack pointer (address of the top of the stack)
    private int sp = 0;

    // stack base (address of the bottom of the stack)
    private int sb = 0;

    // heap pointer (address of the top of the heap)
    private int hp;
    
    // GETEOF byte status for System.in
    private byte eofFlag = 0;

    // true if the virtual computer is currently running
    private boolean running = false;

    /**
     * Construct a CPRL virtual machine with a given number of bytes of memory.
     *
     * @param numBytes The number of bytes in memory of the virtual machine.
     */
    public CVM(int numBytes)
      {
        // create and zero out memory
        memory = new byte[numBytes];
        for (int i = 0; i < memory.length; ++i)
            memory[i] = 0;

        hp = numBytes;
      }

    /**
     * Loads the program into memory.
     *
     * @param codeFile The FileInputStream containing the object code.
     */
    public void loadProgram(FileInputStream codeFile)
      {
        int address = 0;
        int inByte;

        try (codeFile)
          {
            inByte = codeFile.read();
            while (inByte != -1)
              {
                memory[address++] = (byte) inByte;
                inByte = codeFile.read();
              }

            bp = address;
            sb = address;
            sp = bp - 1;
            codeFile.close();
          }
        catch (IOException ex)
          {
            error(ex.toString());
          }
      }

    /**
     * Prints values of internal registers to standard output.
     */
    private void printRegisters()
      {
        out.println("Registers: PC=" + pc + ", BP=" + bp + ", SB=" + sb
                           + ", SP=" + sp + ", HP=" + hp);
      }

    /**
     * Prints a view of memory to standard output.
     */
    private void printMemory()
      {
        int  memAddr = 0;
        byte byte0;
        byte byte1;
        byte byte2;
        byte byte3;

        while (memAddr < sb)
          {
            // Prints "PC ->" in front of the correct memory address
            if (pc == memAddr)
                out.print("PC ->");
            else
                out.print("     ");

            var memAddrStr = String.format("%4s", memAddr);
            var opcode = Opcode.toOpcode(memory[memAddr]);

            if (opcode.isZeroOperandOpcode())
              {
                out.println(memAddrStr + ":  " + opcode);
                ++memAddr;
              }
            else if (opcode.isByteOperandOpcode())
              {
                out.print(memAddrStr + ":  " + opcode);
                ++memAddr;
                out.println(" " + memory[memAddr++]);

              }
            else if (opcode.isIntOperandOpcode())
              {
                out.print(memAddrStr + ":  " + opcode);
                ++memAddr;
                byte0 = memory[memAddr++];
                byte1 = memory[memAddr++];
                byte2 = memory[memAddr++];
                byte3 = memory[memAddr++];
                out.println(" " + ByteUtil.bytesToInt(byte0, byte1, byte2, byte3));
              }
            else if (opcode == Opcode.LDCCH)
              {
                // special case: LDCCH
                out.print(memAddrStr + ":  " + opcode);
                ++memAddr;
                byte0 = memory[memAddr++];
                byte1 = memory[memAddr++];
                out.println(" " + ByteUtil.bytesToChar(byte0, byte1));
              }
            else if (opcode == Opcode.LDCSTR)
              {
                // special case: LDCSTR
                out.print(memAddrStr + ":  " + opcode);
                ++memAddr;
                // now print the string
                out.print("  \"");
                byte0 = memory[memAddr++];
                byte1 = memory[memAddr++];
                byte2 = memory[memAddr++];
                byte3 = memory[memAddr++];
                int strLength = ByteUtil.bytesToInt(byte0, byte1, byte2, byte3);
                for (int i = 0; i < strLength; ++i)
                  {
                    byte0 = memory[memAddr++];
                    byte1 = memory[memAddr++];
                    out.print(ByteUtil.bytesToChar(byte0, byte1));
                   }
                 out.println("\"");
              }
            else
                error("*** PrintMemory: Unknown opcode " + opcode + " ***");
          }

        // now print remaining values that compose the stack
        for (memAddr = sb; memAddr <= sp; ++memAddr)
          {
            // Prints "SB ->", "BP ->", and "SP ->" in front of the correct memory address
            if (sb == memAddr)
                out.print("SB ->");
            else if (bp == memAddr)
                out.print("BP ->");
            else if (sp == memAddr)
                out.print("SP ->");
            else
                out.print("     ");

            var memAddrStr = String.format("%4s", memAddr);
            out.println(memAddrStr + ":  " + memory[memAddr]);
          }

        if (hp < memory.length)
          {
            out.println(".............................................. heap");
        
            out.println("HP -> " + String.format("%4s", hp) + ": " + memory[hp]);
            for (int memAddr2 = hp + 1; memAddr2 < memory.length; ++memAddr2)
              {
                var memAddrStr = String.format("%4s", memAddr2);
                out.println("      " + memAddrStr + ": " + memory[memAddr2]);
              }
          }
      }

    /**
     * Alternative to Console.readline() when CVM runs in an IDE.
     */
    private void readLine()
      {
        int ch;
        try
          {
            do
                ch = System.in.read();
            while (ch != '\n');
          }
        catch (IOException ex)
          {
            error(ex.toString());
          }
      }

    /**
     * Prompt user and wait for user to press the enter key.
     */
    private void pause()
      {
        System.out.println("Press enter to continue...");
        Console console = System.console();
        if (console == null)
            readLine();
        else
            console.readLine();
      }

    /**
     * Runs the program currently in memory.
     */
    public void run()
      {
        running = true;
        pc = 0;

        try
          {
            while (running)
              {
                if (DEBUG)
                  {
                    printRegisters();
                    printMemory();
                    pause();
                  }

                switch (Opcode.toOpcode(fetchByte()))
                  {
                    case ADD      -> add();
                    case ALLOC    -> allocate();
                    case BITAND   -> bitAnd();
                    case BITOR    -> bitOr();
                    case BITXOR   -> bitXor();
                    case BITNOT   -> bitNot();
                    case BR       -> branch();
                    case BE       -> branchEqual();
                    case BNE      -> branchNotEqual();
                    case BG       -> branchGreater();
                    case BGE      -> branchGreaterOrEqual();
                    case BL       -> branchLess();
                    case BLE      -> branchLessOrEqual();
                    case BZ       -> branchZero();
                    case BNZ      -> branchNonZero();
                    case BYTE2INT -> byteToInt();
                    case CALL     -> call();
                    case CHAR2INT -> charToInt();
                    case DEC      -> decrement();
                    case DIV      -> divide();
                    case GETEOF   -> getEof();
                    case GETBYTE  -> getByte();
                    case GETCH    -> getCh();
                    case GETINT   -> getInt();
                    case GETSTR   -> getString();
                    case HALT     -> halt();
                    case INC      -> increment();
                    case INT2BYTE -> intToByte();
                    case INT2CHAR -> intToChar();
                    case LDCB     -> loadConstByte();
                    case LDCB0    -> loadConstByte0();
                    case LDCB1    -> loadConstByte1();
                    case LDCCH    -> loadConstCh();
                    case LDCINT   -> loadConstInt();
                    case LDCINT0  -> loadConstInt0();
                    case LDCINT1  -> loadConstInt1();
                    case LDCSTR   -> loadConstStr();
                    case LDLADDR  -> loadLocalAddress();
                    case LDGADDR  -> loadGlobalAddress();
                    case LOAD     -> load();
                    case LOADB    -> loadByte();
                    case LOAD2B   -> load2Bytes();
                    case LOADW    -> loadWord();
                    case MOD      -> modulo();
                    case MUL      -> multiply();
                    case NEG      -> negate();
                    case NOT      -> not();
                    case PROC     -> procedure();
                    case PROGRAM  -> program();
                    case PUTCH    -> putChar();
                    case PUTEOL   -> putEOL();
                    case PUTINT   -> putInt();
                    case PUTSTR   -> putString();
                    case RET      -> returnInst();
                    case RET0     -> returnZero();
                    case RET4     -> returnFour();
                    case SHL      -> shl();
                    case SHR      -> shr();
                    case STORE    -> store();
                    case STOREB   -> storeByte();
                    case STORE2B  -> store2Bytes();
                    case STOREW   -> storeWord();
                    case STRCAT   -> strcat();
                    case SUB      -> subtract();
                    default       -> error("invalid machine instruction");
                  }
              }
          }
        catch (Throwable t)
          {
            System.err.println("Virtual Machine Error: " + t.getMessage());
            t.printStackTrace();
          }
      }

    // Start: helper functions and internal machine instructions
    // ---------------------------------------------------------

    /**
     * Print an error message and exit with nonzero status code.
     */
    private static void error(String message)
      {
        System.err.println(message);
        System.exit(1);
      }

    /**
     * Pop the top byte off the stack and return its value.
     */
    private byte popByte()
      {
        return memory[sp--];
      }

    /**
     * Pop the top character off the stack and return its value.
     */
    private char popChar()
      {
        byte b1 = popByte();
        byte b0 = popByte();

        return ByteUtil.bytesToChar(b0, b1);
      }

    /**
     * Pop the top integer off the stack and return its value.
     */
    private int popInt()
      {
        byte b3 = popByte();
        byte b2 = popByte();
        byte b1 = popByte();
        byte b0 = popByte();
        return ByteUtil.bytesToInt(b0, b1, b2, b3);
      }

    /**
     * Push a byte onto the stack.
     */
    private void pushByte(byte b)
      {
        if (++sp >= memory.length)
            error("*** Out of memory ***");
        memory[sp] = b;
      }

    /**
     * Push a character onto the stack.
     */
    private void pushChar(char c)
      {
        byte[] bytes = ByteUtil.charToBytes(c);
        pushByte(bytes[0]);
        pushByte(bytes[1]);
      }

    /**
     * Push an integer onto the stack.
     */
    private void pushInt(int n)
      {
        byte[] bytes = ByteUtil.intToBytes(n);
        pushByte(bytes[0]);
        pushByte(bytes[1]);
        pushByte(bytes[2]);
        pushByte(bytes[3]);
      }

    /**
     * Fetch the next instruction/byte from memory.
     */
    private byte fetchByte()
      {
        return memory[pc++];
      }

    /**
     * Fetch the next character operand from memory.
     */
    private char fetchChar()
      {
        byte b0 = fetchByte();
        byte b1 = fetchByte();
        return ByteUtil.bytesToChar(b0, b1);
      }

    /**
     * Fetch the next integer operand from memory.
     */
    private int fetchInt()
      {
        byte b0 = fetchByte();
        byte b1 = fetchByte();
        byte b2 = fetchByte();
        byte b3 = fetchByte();
        return ByteUtil.bytesToInt(b0, b1, b2, b3);
      }

    /**
     * Returns the character at the specified memory address.
     * Does not alter pc, sp, or bp.
     */
    private char getCharAtAddr(int address)
      {
        byte b0 = memory[address + 0];
        byte b1 = memory[address + 1];
        return ByteUtil.bytesToChar(b0, b1);
      }

    /**
     * Returns the integer at the specified memory address.
     * Does not alter pc, sp, or bp.
     */
    private int getIntAtAddr(int address)
      {
        byte b0 = memory[address + 0];
        byte b1 = memory[address + 1];
        byte b2 = memory[address + 2];
        byte b3 = memory[address + 3];
        return ByteUtil.bytesToInt(b0, b1, b2, b3);
      }

    /**
     * Returns the word at the specified memory address.
     * Does not alter pc, sp, or bp.
     */
    private int getWordAtAddr(int address)
      {
        return getIntAtAddr(address);
      }

    /**
     * Writes the char value to the specified memory address.
     * Does not alter pc, sp, or bp.
     */
    private void putCharToAddr(char value, int address)
      {
        byte[] bytes = ByteUtil.charToBytes(value);
        memory[address + 0] = bytes[0];
        memory[address + 1] = bytes[1];
      }

    /**
     * Writes the integer value to the specified memory address.
     * Does not alter pc, sp, or bp.
     */
    private void putIntToAddr(int value, int address)
      {
        byte[] bytes = ByteUtil.intToBytes(value);
        memory[address + 0] = bytes[0];
        memory[address + 1] = bytes[1];
        memory[address + 2] = bytes[2];
        memory[address + 3] = bytes[3];
      }

    /**
     * Writes the word value to the specified memory address.
     * Does not alter pc, sp, or bp.
     */
    private void putWordToAddr(int word, int address)
      {
        putIntToAddr(word, address);
      }

    /**
     * Allocate the specified number of bytes on the heap.
     */
    private void heapAlloc(int numBytes)
      {
        hp = hp - numBytes;
        if (hp <= sp)
          error("*** Out of memory ***");
      }

    /**
     * Copy the specified string to the heap.
     */
    private void copyStringToHeap(String s)
      {
        // allocate space on the heap for the sequence of characters
        heapAlloc(Constants.BYTES_PER_CHAR*s.length());
        var hpTemp = hp;
        for (int i = 0; i < s.length(); ++i)
          {
            putCharToAddr(s.charAt(i), hpTemp);
            hpTemp = hpTemp + Constants.BYTES_PER_CHAR;
          }

        // allocate space on the heap for length
        heapAlloc(Constants.BYTES_PER_INTEGER);
        putIntToAddr(s.length(), hp);
      }

    /**
     * Copy sequence of characters from the specified string address to the heap.
     */
    private void copyStringToHeap(int strAddress)
      {
        var strLength = getIntAtAddr(strAddress);
        
        // allocate space on the heap for the sequence of characters
        heapAlloc(Constants.BYTES_PER_CHAR*strLength);

        // skip over length
        var firstCharAddress = strAddress + Constants.BYTES_PER_INTEGER;

        for (int i = 0; i < strLength; ++i)
          {
            var sourceAddress = firstCharAddress + i*Constants.BYTES_PER_CHAR;
            var ch = getCharAtAddr(sourceAddress);
            var destAddress = hp + i*Constants.BYTES_PER_CHAR;
            putCharToAddr(ch, destAddress);
          }
      }

    // -------------------------------------------------------
    // End: helper functions and internal machine instructions
    // Start: machine instructions corresponding to opcodes
    // -------------------------------------------------------

    private void add()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        pushInt(operand1 + operand2);
      }

    private void allocate()
      {
        int numBytes = fetchInt();
        sp = sp + numBytes;
        if (sp >= hp)
            error("*** Out of memory ***");
      }

    private void bitAnd()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        pushInt(operand1 & operand2);
      }

    private void bitOr()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        pushInt(operand1 | operand2);
      }

    private void bitXor()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        pushInt(operand1 ^ operand2);
      }

    private void bitNot()
      {
        int operand = popInt();
        pushInt(~operand);
      }

    private void branch()
      {
        int displacement = fetchInt();
        pc = pc + displacement;
      }

    private void branchEqual()
      {
        int displacement = fetchInt();
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand1 == operand2)
            pc = pc + displacement;
      }

    private void branchNotEqual()
      {
        int displacement = fetchInt();
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand1 != operand2)
            pc = pc + displacement;
      }

    private void branchGreater()
      {
        int displacement = fetchInt();
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand1 > operand2)
            pc = pc + displacement;
      }

    private void branchGreaterOrEqual()
      {
        int displacement = fetchInt();
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand1 >= operand2)
            pc = pc + displacement;
      }

    private void branchLess()
      {
        int displacement = fetchInt();
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand1 < operand2)
            pc = pc + displacement;
      }

    private void branchLessOrEqual()
      {
        int displacement = fetchInt();
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand1 <= operand2)
            pc = pc + displacement;
      }

    private void branchZero()
      {
        int  displacement = fetchInt();
        byte value = popByte();

        if (value == 0)
            pc = pc + displacement;
      }

    private void branchNonZero()
      {
        int  displacement = fetchInt();
        byte value = popByte();

        if (value != 0)
            pc = pc + displacement;
      }

    private void byteToInt()
      {
        byte b = popByte();
        pushInt(ByteUtil.byteToInt(b));
      }

    private void call()
      {
        int displacement = fetchInt();

        pushInt(bp);   // dynamic link
        pushInt(pc);   // return address

        // set bp to starting address of new frame
        bp = sp - Constants.BYTES_PER_CONTEXT + 1;

        // set pc to first statement of called procedure
        pc = pc + displacement;
      }

    private void charToInt()
      {
        char c = popChar();
        pushInt(ByteUtil.charToInt(c));
      }

    private void decrement()
      {
        int operand = popInt();
        pushInt(operand - 1);
      }

    private void divide()
      {
        int operand2 = popInt();
        int operand1 = popInt();

        if (operand2 != 0)
            pushInt(operand1/operand2);
        else
            error("*** FAULT: Divide by zero ***");
      }

    private void getEof()
      {
        pushByte(eofFlag);   // push eofFlag status byte
      }

    private void getByte()
      {
        try
          {
            // use System.in temporarily to read one byte from stdin
            int byteValue = System.in.read();
            
            if (byteValue == EOF)
              {
                eofFlag = 1;
                byteValue = 0;
              }

            int destAddr = popInt();
            byte b = (byte) byteValue;
            memory[destAddr] = b;
          }
        catch (IOException ex)
          {
            error(ex.toString());
          }
      }

    private void getCh()
      {
        try
          {
            int destAddr = popInt();
            int n = reader.read();

            if (n == EOF)
              {
                eofFlag = 1;
                n = 0;
              }

            putCharToAddr((char) n, destAddr);
          }
        catch (IOException ex)
          {
            error(ex.toString());
          }
      }

    private void getInt()
      {
        try
          {
            int destAddr = popInt();
            int n = 0;
            if (scanner.hasNextInt())
                n = scanner.nextInt();
            else
                eofFlag = 1;
            putIntToAddr(n, destAddr);
          }
        catch (NumberFormatException ex)
          {
            error(ex.toString());
          }
      }

    private void getString()
      {
        int destAddr = popInt();
        var data = "";

        if (scanner.hasNextLine())
            data = scanner.nextLine();
        else
            eofFlag = 1;
        
        copyStringToHeap(data);
        
        // copy address of string to the variable
        putIntToAddr(hp, destAddr);
      }

    private void halt()
      {
        running = false;
      }

    private void increment()
      {
        int operand = popInt();
        pushInt(operand + 1);
      }

    private void intToByte()
      {
        int n = popInt();
        pushByte(ByteUtil.intToByte(n));
      }

    private void intToChar()
      {
        int n = popInt();
        pushChar(ByteUtil.intToChar(n));
      }

    private void load()
      {
        int length  = fetchInt();
        int address = popInt();

        for (int i = 0; i < length; ++i)
            pushByte(memory[address + i]);
      }

    private void loadConstByte()
      {
        byte b = fetchByte();
        pushByte(b);
      }

    private void loadConstByte0()
      {
        pushByte((byte) 0);
      }

    private void loadConstByte1()
      {
        pushByte((byte) 1);
      }

    private void loadConstCh()
      {
        char ch = fetchChar();
        pushChar(ch);
      }

    private void loadConstInt()
      {
        int value = fetchInt();
        pushInt(value);
      }

    private void loadConstInt0()
      {
        pushInt(0);
      }

    private void loadConstInt1()
      {
        pushInt(1);
      }

    private void loadConstStr()
      {
        // At this point pc is the address of the constant string.
        var address = pc;
        pushInt(address);   
        
        // update pc to skip over string length and characters
        var length = getIntAtAddr(address);
        pc = pc + Constants.BYTES_PER_INTEGER + length*Constants.BYTES_PER_CHAR;
      }

    private void loadLocalAddress()
      {
        int displacement = fetchInt();
        pushInt(bp + displacement);
      }

    private void loadGlobalAddress()
      {
        int displacement = fetchInt();
        pushInt(sb + displacement);
      }

    private void loadByte()
      {
        int  address = popInt();
        byte b = memory[address];
        pushByte(b);
      }

    private void load2Bytes()
      {
        int  address = popInt();
        byte b0 = memory[address + 0];
        byte b1 = memory[address + 1];
        pushByte(b0);
        pushByte(b1);
      }

    private void loadWord()
      {
        int address = popInt();
        int word = getWordAtAddr(address);
        pushInt(word);
      }

    private void modulo()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        pushInt(operand1%operand2);
      }

    private void multiply()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        pushInt(operand1*operand2);
      }

    private void negate()
      {
        int operand1 = popInt();
        pushInt(-operand1);
      }

    private void not()
      {
        byte operand = popByte();
        pushByte(operand == FALSE ? TRUE : FALSE);
      }

    private void procedure()
      {
        allocate();
      }

    private void program()
      {
        int varLength = fetchInt();

        bp = sb;
        sp = bp + varLength - 1;

        if (sp >= memory.length)
            error("*** Out of memory ***");
      }

    private void putChar()
      {
        out.print(popChar());
      }

    private void putInt()
      {
        out.print(popInt());
      }

    private void putEOL()
      {
        out.println();
      }

    private void putString()
      {
        int address = popInt();
        int length  = getIntAtAddr(address);

        // skip over length
        address = address + Constants.BYTES_PER_INTEGER;

        for (int i = 0; i < length; ++i)
          {
            out.print(getCharAtAddr(address));
            address = address + Constants.BYTES_PER_CHAR;
          }
      }

    private void returnInst()
      {
        int paramLength = fetchInt();
        pc = getIntAtAddr(bp + Constants.BYTES_PER_INTEGER);
        sp = bp - paramLength - 1;
        bp = getIntAtAddr(bp);
      }

    private void returnZero()
      {
        pc = getIntAtAddr(bp + Constants.BYTES_PER_INTEGER);
        sp = bp - 1;
        bp = getIntAtAddr(bp);
      }

    private void returnFour()
      {
        pc = getIntAtAddr(bp + Constants.BYTES_PER_INTEGER);
        sp = bp - 5;
        bp = getIntAtAddr(bp);
      }

    private void shl()
      {
        int operand2 = popInt();
        int operand1 = popInt();

        // zero out all except rightmost 5 bits of shiftAmount
        int shiftAmount = operand2 & 0b11111;

        pushInt(operand1 << shiftAmount);
      }

    private void shr()
      {
        int operand2 = popInt();
        int operand1 = popInt();

        // zero out all except rightmost 5 bits of shiftAmount
        int shiftAmount = operand2 & 0b11111;

        pushInt(operand1 >> shiftAmount);
      }

    private void store()
      {
        int length   = fetchInt();
        int destAddr = getIntAtAddr(sp - length - 3);

        // pop bytes of data, storing in reverse order
        for (int i = length - 1; i >= 0; --i)
            memory[destAddr + i] = popByte();

        popInt();   // remove destAddr from stack
      }

    private void storeByte()
      {
        byte value = popByte();
        int  destAddr = popInt();
        memory[destAddr] = value;
      }

    private void store2Bytes()
      {
        byte byte1 = popByte();
        byte byte0 = popByte();
        int  destAddr = popInt();
        memory[destAddr + 0] = byte0;
        memory[destAddr + 1] = byte1;
      }

    private void storeWord()
      {
        int value = popInt();
        int destAddr = popInt();
        putWordToAddr(value, destAddr);
      }

    private void strcat()
      {
        // address of operands are pushed onto the stack in reverse order
        var addressOfLeftOperand  = popInt();
        var addressOfRightOperand = popInt();
        var strLengthLeft  = getIntAtAddr(addressOfLeftOperand);
        var strLengthRight = getIntAtAddr(addressOfRightOperand);
        var strLengthConcatenated = strLengthLeft + strLengthRight;
        
        copyStringToHeap(addressOfLeftOperand);
        copyStringToHeap(addressOfRightOperand);
        // allocate space on the heap for combined length
        heapAlloc(Constants.BYTES_PER_INTEGER);
        putIntToAddr(strLengthConcatenated, hp);

        pushInt(hp);   // hp is the address of the concatenated strings
      }

    private void subtract()
      {
        int operand2 = popInt();
        int operand1 = popInt();
        int result   = operand1 - operand2;
        pushInt(result);
      }
  }
