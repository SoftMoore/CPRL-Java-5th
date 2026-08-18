package cprlc.ast;

import common.CodeGenException;

import java.util.List;

/**
 * The abstract syntax tree node for a compound statement.
 */
public class CompoundStmt extends Statement
  {
    // the list of statements in the compound statement
    private List<Statement> statements;

    public CompoundStmt(List<Statement> statements)
      {
        this.statements = statements;
      }

    /**
     * Returns the list of statements in this compound statement.
     */
    public List<Statement> statements()
      {
        return statements;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
// ...
      }
  }
