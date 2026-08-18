package cprlc.ast;

import common.CodeGenException;
import common.ConstraintException;

import cprlc.ArrayType;
import cprlc.RecordType;
import cprlc.ScopeLevel;
import cprlc.Token;
import cprlc.Type;

import java.util.List;

/**
 * The abstract syntax tree node for a variable, which is any named
 * variable that can appear on the left-hand side of an assignment
 * statement or as part of an expression.
 */
public class Variable extends Expression
  {
    private boolean useAsExpression = false;  // set to true to emit expression value
    private Token idToken;
    private List<Expression> selectorExprs;
    private VariableDecl decl;                // nonstructural reference

    /**
     * Construct a variable with a reference to its declaration,
     * its identifier token, and a list of selector expressions.
     */
    public Variable(VariableDecl decl, Token idToken, List<Expression> selectorExprs)
      {
        super(decl.type(), idToken.position());
        this.decl = decl;
        this.idToken = idToken;
        this.selectorExprs = selectorExprs;
      }

    /**
     * Set to true if this variable is used as an expression.
     */
    public void setUseAsExpression(boolean useAsExpression)
      {
        this.useAsExpression = useAsExpression;
      }

    /**
     * Returns the declaration for this variable.
     */
    public VariableDecl decl()
      {
        return decl;
      }

    /**
     * Returns the identifier token for this variable.
     */
    public Token idToken()
      {
        return idToken;
      }

    /**
     * Returns the list of selector expressions for the variable.  Returns an
     * empty list if the variable is not an array, string, or record variable.
     */
    public List<Expression> selectorExprs()
      {
        return selectorExprs;
      }

    /**
     * Check an array selector expression for this variable.
     */
    private void checkArraySelectorExpr(Expression selectorExpr, ArrayType arrayType)
      {
        try
          {
            // Applying the selector effectively changes the
            // variable's type to the element type of the array.
            setType(arrayType.elementType());

            // check that the selector expression is not a field expression
// ...

            // check index expression for valid index type
// ...
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    /**
     * Check a record selector expression for this variable.
     */
    private void checkRecordSelectorExpr(Expression selectorExpr, RecordType recType)
      {
        try
          {
            // check that the selector expression is a field expression
// ...

            // Applying the selector effectively changes the
            // variable's type to the type of the field.
            var fieldExpr = (FieldExpr) selectorExpr;
            var fieldId   = fieldExpr.fieldId();

            if (recType.containsField(fieldId.text()))
              {
                var fieldDecl = recType.get(fieldId.text());
                fieldExpr.setFieldDecl(fieldDecl);
                setType(fieldDecl.type());
              }
            else
              {
                var errorMsg = "\"" + fieldId.text()
                             + "\" is not a valid field name for " + recType + ".";
                throw error(fieldId.position(), errorMsg);
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    /**
     * Check a string selector expression for this variable.
     */
    private void checkStringSelectorExpr(Expression selectorExpr)
      {
        try
          {
            // Selector can be field expression .length (type Integer)
            // or an index expression for the characters (type Char).

            if (selectorExpr instanceof FieldExpr fieldExpr)
              {
                // Applying length field selector effectively changes
                // the variable's type to Integer.
// ...

                // check that the field identifier is "length"
// ...
              }
            else
              {
                // Must be an index expression.  Applying an index selector
                // effectively changes the variable's type to Char.
// ...

                // must be an index expression; check for valid index type
// ...
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            assert decl instanceof VarDecl || decl instanceof ParameterDecl;

            for (var expr : selectorExprs)
              {
                expr.checkConstraints();

                // Each selector expression must correspond to
                // an array type, a record type, or type String.

                if (type() instanceof ArrayType arrayType)
                    checkArraySelectorExpr(expr, arrayType);
                else if (type() instanceof RecordType recType)
                    checkRecordSelectorExpr(expr, recType);
                else if (type() == Type.String)
                    checkStringSelectorExpr(expr);
                else
                  {
                    var errorMsg = "Selector expression not allowed for variable of type "
                                 + type();
                    throw error(expr.position(), errorMsg);
                  }
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    /**
     * Emit selector expressions for this variable.
     */
    private void emitSelectors() throws CodeGenException
      {
        var type = decl.type();

        // For an array, record, or string, at this point the base address
        // of the variable is on the top of the stack.  We need to replace
        // it by the sum base address + offset
        for (var expr : selectorExprs)
          {
            if (type instanceof ArrayType arrayType)
              {
                expr.emit();   // emit the index

                // multiply by size of array element type to get offset
// ...

                // Note: No code to perform bounds checking for the index to
                // ensure that the index is >= 0 and < number of elements.

                emit("ADD");   // add offset to the base address

                type = arrayType.elementType();
              }
            else if (type instanceof RecordType)
              {
                var fieldExpr = (FieldExpr) expr;

                if (fieldExpr.fieldDecl().offset() != 0)
                  {
                    // add offset to the base address
// ...
                  }

                type = fieldExpr.fieldDecl().type();
              }
            else if (type == Type.String)
              {
                emit("LOADW");   // leaves address of string value on top of stack

                if (expr instanceof FieldExpr)
                  {
                    // The only allowed field expression for strings is length, which
                    // is at offset 0; we don't need to emit code for the offset.
                  }
                else   // selector expression must be an index expression
                  {
                    // skip over length (type Integer)
                    emit("LDCINT " + Type.Integer.size());
                    emit("ADD");

                    expr.emit();   // emit index expression

                    // multiply by size of type Char to get offset
                    emit("LDCINT " + Type.Char.size());
                    emit("MUL");

                    emit("ADD");   // add offset to the base address

                    // Note: No code to perform bounds checking for the index to
                    // ensure that the index is >= 0 and < string length.
                  }
              }
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        if (decl instanceof ParameterDecl pDecl && pDecl.isVarParam())
          {
            // address of actual parameter is value of var parameter
            emit("LDLADDR " + pDecl.relAddr());
            emit("LOADW");
          }
        else if (decl.scopeLevel() == ScopeLevel.GLOBAL)
            emit("LDGADDR " + decl.relAddr(idToken.text()));
        else
            emit("LDLADDR " + decl.relAddr(idToken.text()));

        emitSelectors();

        if (useAsExpression)
          {
            // Previous code leaves address of this variable on top
            // of stack.  We need to emit the value at that address.
            // Promote Byte values to Integer if necessary.
            emitLoadInst(type());
            if (type() == Type.Byte)
              {
                emit("BYTE2INT");
                setType(Type.Integer);
              }
          }
      }
  }
