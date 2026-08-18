package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.Type;

/**
 * The abstract syntax tree node for a read statement.
 */
public class ReadStmt extends Statement
  {
    private Variable variable;

    /**
     * Construct a read statement with the specified variable for storing the input.
     */
    public ReadStmt(Variable variable)
      {
        this.variable = variable;
      }

    @Override
    public void checkConstraints()
      {
        // input is limited to numeric types, characters, and strings
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
        variable.emit();

        if (variable.type() == Type.String)
            emit("GETSTR");
        else if (variable.type() == Type.Integer)
            emit("GETINT");
        else if (variable.type() == Type.Char)
            emit("GETCH");
        else if (variable.type() == Type.Byte)
            emit("GETBYTE");
        else
          {
            var errorMsg = "Input supported only for numeric types, characters, and strings.";
            throw new CodeGenException(variable.idToken().position(), errorMsg);
          }
      }
  }
