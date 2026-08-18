package cprlc.builtins;

/**
 * Interface for all built-in subprograms.
 */
public interface BuiltinSubprogram
  {
    /**
     * Set value to true if this built-in subprogram is called.
     * Otherwise, object code will not be emitted.
     */
    public void setIsCalled(boolean isCalled);
  }
