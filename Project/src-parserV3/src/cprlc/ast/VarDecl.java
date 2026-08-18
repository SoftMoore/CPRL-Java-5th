package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;
import common.InternalCompilerException;

import cprlc.ArrayType;
import cprlc.RecordType;
import cprlc.ScopeLevel;
import cprlc.Token;
import cprlc.Type;

import java.util.*;

/**
 * The abstract syntax tree node for a var declaration.
 */
final public class VarDecl extends InitialDecl implements VariableDecl
  {
    private List<Token>  identifiers;
    private List<String> idNames;
    private Initializer  initializer;
    private ScopeLevel   scopeLevel;
    private Map<String, Integer> relAddrMap = new HashMap<>();


    /**
     * Construct a var declaration with its list of identifier tokens,
     * type, initializer, and scope level
     */
    public VarDecl(List<Token> identifiers, Type varType,
                   Initializer initializer, ScopeLevel scopeLevel)
      {
        super(new Token(), varType);   // not a single token for a varDecl
        this.identifiers = identifiers;
        this.initializer = initializer;
        this.scopeLevel  = scopeLevel;
        idNames = new ArrayList<String>();

        for (var id : identifiers)
            idNames.add(id.text());
      }

    /**
     * Returns the list of identifier names for this var declaration.
     */
    public List<String> idNames()
      {
        return idNames;
      }

    @Override
    public int size()
      {
        return type().size();
      }

    @Override
    public ScopeLevel scopeLevel()
      {
        return scopeLevel;
      }

    @Override
    public void setRelAddr(String idName, int relAddr)
      {
        relAddrMap.put(idName, relAddr);
      }

    @Override
    public int relAddr(String idName)
      {
        return relAddrMap.get(idName);
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            // check constraints only if initializer is not empty
            if (!initializer.isEmpty())
                checkInitializer(type(), initializer);
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    private void checkInitializer(Type type, Initializer initializer)
        throws ConstraintException
      {
        if (type.isScalarType() || type == Type.String)
          {
            // initializer must be a ConstValue or an EnumConstValue of the appropriate type
            if (initializer instanceof ConstValue constValue)
              {
                // check that the initializer has the correct type
                if (!matchTypes(type, constValue))
                  {
                    var errorMsg = "Type mismatch for variable initialization.";
                    throw error(initializer.position(), errorMsg);
                  }

                if (type == Type.Byte)
                  {
                    if (constValue.intValue() < 0 || constValue.intValue() > 255)
                      {
                        var errorMsg = "Initializer for type Byte must be in the range 0..255.";
                        throw error(initializer.position(), errorMsg);
                      }

                    constValue.setType(Type.Byte);   // default is Integer
                  }
              }
            else
              {
                var errorMsg = "Initializer must be a constant value.";
                throw error(initializer.position(), errorMsg);
              }
          }
        else if (type instanceof ArrayType arrayType)
          {
            // must be a composite initializer with correct number of values
            if (initializer instanceof CompositeInitializer compositeInitializer)
              {
                var initializers = compositeInitializer.initializers();
                if (initializers.size() != arrayType.numElements())
                  {
                    var errorMsg = "Incorrect number of initializers for array type "
                                 + arrayType + ".";
                    throw error(initializer.position(), errorMsg);
                  }

                for (var i : initializers)
                    checkInitializer(arrayType.elementType(), i);
              }
            else
              {
                var errorMsg = "Initializer for an array must be composite.";
                throw error(initializer.position(), errorMsg);
              }
          }
        else if (type instanceof RecordType recordType)
          {
            // initializer must be composite with correct number of values and types
            if (initializer instanceof CompositeInitializer compositeInitializer)
              {
                var initializers = compositeInitializer.initializers();
                var fieldDecls   = recordType.fieldDecls();
                if (initializers.size() != fieldDecls.size())
                  {
                    var errorMsg = "Incorrect number of initializers for record type "
                                 + recordType + ".";
                    throw error(initializer.position(), errorMsg);
                  }

                for (int i = 0; i < initializers.size(); ++i)
                    checkInitializer(fieldDecls.get(i).type(), initializers.get(i));
              }
            else
              {
                var errorMsg = "Initializer for a record must be composite.";
                throw error(initializer.position(), errorMsg);
              }
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        // emit code only if the initializer is not empty
        if (!initializer.isEmpty())
          {
            for (var identifier : identifiers)
              {
                // load the address of the variable
                if (scopeLevel == ScopeLevel.GLOBAL)
                    emit("LDGADDR " + relAddr(identifier.text()));
                else
                    emit("LDLADDR " + relAddr(identifier.text()));

                if (initializer instanceof ConstValue constValue)
                  {
                    constValue.emit();
                    emitStoreInst(constValue.type());
                  }
                else if (initializer instanceof CompositeInitializer compositeInitializer)
                  {
                    compositeInitializer.emit();
                    emitStoreInst(compositeInitializer.size());
                  }
                else
                  {
                    var errorMsg = "Unexpected initializer type.";
                    throw new InternalCompilerException(position(), errorMsg);
                  }
              }
          }
      }
  }
