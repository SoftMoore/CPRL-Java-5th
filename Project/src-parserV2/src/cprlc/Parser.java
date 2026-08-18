package cprlc;

import common.ErrorHandler;
import common.InternalCompilerException;
import common.ParserException;
import common.Position;

import java.io.IOException;
import java.util.*;

/**
 * This class uses recursive descent to perform syntax analysis of
 * the CPRL source language.
 */
public final class Parser
  {
    private Scanner scanner;
    private IdTable idTable;
    private ErrorHandler errorHandler;

    /**
     * Symbols that can follow a statement.
     */
    private final Set<Symbol> stmtFollowers = EnumSet.of(
// ...
      );

    /**
     * Symbols that can follow an initializer.
     */
    private final Set<Symbol> initializerFollowers = EnumSet.of(
// ...
      );

    /**
     * Symbols that can follow a subprogram declaration.
     */
    private final Set<Symbol> subprogDeclFollowers = EnumSet.of(
// ...
      );

    /**
     * Symbols that can follow a factor.
     */
    private final Set<Symbol> factorFollowers = EnumSet.of(
        Symbol.semicolon,   Symbol.loopRW,      Symbol.thenRW,
        Symbol.rightParen,  Symbol.andRW,       Symbol.orRW,
        Symbol.equals,      Symbol.notEqual,    Symbol.lessThan,
        Symbol.lessOrEqual, Symbol.greaterThan, Symbol.greaterOrEqual,
        Symbol.plus,        Symbol.minus,       Symbol.times,
        Symbol.divide,      Symbol.modRW,       Symbol.rightBracket,
        Symbol.comma,       Symbol.bitwiseAnd,  Symbol.bitwiseOr,
        Symbol.bitwiseXor,  Symbol.leftShift,   Symbol.rightShift,
        Symbol.dotdot,      Symbol.colon,       Symbol.questionMark);

    /**
     * Symbols that can follow an initial declaration (computed property).
     * Set is computed dynamically based on the scope level.
     */
    private Set<Symbol> initialDeclFollowers()
      {
        // An initial declaration can always be followed by another
        // initial declaration, regardless of the scope level.
        var followers = EnumSet.of(Symbol.constRW, Symbol.varRW, Symbol.typeRW);

        if (idTable.scopeLevel() == ScopeLevel.GLOBAL)
            followers.addAll(EnumSet.of(Symbol.procRW, Symbol.funRW));
        else
          {
            followers.addAll(stmtFollowers);
            followers.remove(Symbol.elseRW);
          }

        return followers;
      }

    /**
     * Construct a parser with the specified scanner, identifier
     * table, and error handler.
     */
    public Parser(Scanner scanner, IdTable idTable, ErrorHandler errorHandler)
      {
        this.scanner = scanner;
        this.idTable = idTable;
        this.errorHandler = errorHandler;
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>program = initialDecls subprogramDecls .</code>
     */
    public void parseProgram() throws IOException
      {
        try
          {
            parseInitialDecls();
            parseSubprogramDecls();

            // match(Symbol.GETEOF)
            // Let's generate a better error message than "Expecting "End-of-File" but ..."
            if (scanner.symbol() != Symbol.EOF)
              {
                var errorMsg = "Expecting \"proc\" or \"fun\" but found \""
                             + scanner.text() + "\" instead.";
                throw error(errorMsg);
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.EOF));
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>initialDecls = { initialDecl } .</code>
     */
    private void parseInitialDecls() throws IOException
      {
        while (scanner.symbol().isInitialDeclStarter())
            parseInitialDecl();
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>initialDecl = constDecl | varDecl | typeDecl .</code>
     */
    private void parseInitialDecl() throws IOException
      {
// ...   throw an internal error if the symbol is not one of constRW, varRW, or typeRW
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>constDecl = "const" constId ":=" [ "-" ] literal ";" .</code>
     */
    private void parseConstDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>literal = intLiteral | charLiteral | stringLiteral | "true" | "false" .</code>
     */
    private void parseLiteral() throws IOException
      {
        try
          {
            if (scanner.symbol().isLiteral())
                matchCurrentSymbol();
            else
                throw error("Invalid literal expression.");
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>varDecl = "var" identifiers ":" ( typeName | arrayTypeConstr )
     *               [ ":=" initializer] ";" .</code>
     */
    private void parseVarDecl() throws IOException
      {
        try
          {
            match(Symbol.varRW);
            var identifiers = parseIdentifiers();
            match(Symbol.colon);

            var symbol = scanner.symbol();
            if (symbol.isPredefinedType() || symbol == Symbol.identifier)
                parseTypeName();
            else if (symbol == Symbol.arrayRW)
                parseArrayTypeConstr();
            else
              {
                // Add declarations to IdTable to prevent future
                // "has not been declared" error messages.
                for (var id : identifiers)
                    idTable.add(id, IdType.variableId);

                var errorMsg = "Expecting a type name or reserved word \"array\".";
                throw error(errorMsg);
              }

            if (scanner.symbol() == Symbol.assign)
              {
                matchCurrentSymbol();
                parseInitializer();
              }

            for (var id : identifiers)
                idTable.add(id, IdType.variableId);

            match(Symbol.semicolon);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initialDeclFollowers());
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>identifiers = identifier { "," identifier } .</code>
     *
     * @return the list of identifier tokens.  Returns an empty list if parsing fails.
     */
    private List<Token> parseIdentifiers() throws IOException
      {
        try
          {
            var identifiers = new ArrayList<Token>(10);
            var idToken = scanner.token();
            match(Symbol.identifier);
            identifiers.add(idToken);

            while (scanner.symbol() == Symbol.comma)
              {
                matchCurrentSymbol();
                idToken = scanner.token();
                match(Symbol.identifier);
                identifiers.add(idToken);
              }

            return identifiers;
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.colon, Symbol.greaterThan));
            return Collections.emptyList();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>initializer = constValue | compositeInitializer .</code>
     */
    private void parseInitializer() throws IOException
      {
        try
          {
            var symbol = scanner.symbol();
            if (symbol.isLiteral() || symbol == Symbol.minus)
                parseConstValue();
            else if (symbol == Symbol.identifier)
              {
                // Two possible cases: a declared constant or an enum constant
                // value.  Use declaration to determine correct parsing action.
                var idStr  = scanner.text();
                var idType = idTable.get(idStr);

                if (idType != null)
                  {
                    if (idType == IdType.constantId)
                        parseConstValue();
                    else if (idType == IdType.enumTypeId)
                        parseEnumConstValue();
                    else
                      {
                        var errorPos = scanner.position();
                        var errorMsg = "Identifier \"" + idStr
                                 + "\" is not valid as an expression.";

                        throw error(errorPos, errorMsg);
                      }
                  }
                else
                  {
                    throw error("Identifier \"" + idStr + "\" has not been declared.");
                  }

              }
            else if (symbol == Symbol.leftBrace)
                parseCompositeInitializer();
            else
              {
                var errorMsg = "Expecting literal, identifier, or left brace.";
                throw error(errorMsg);
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initializerFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>compositeInitializer = "{" initializer { "," initializer } "}" .</code>
     */
    private void parseCompositeInitializer() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>typeDecl = arrayTypeDecl | recordTypeDecl | enumTypeDecl .</code>
     */
    private void parseTypeDecl() throws IOException
      {
        assert scanner.symbol() == Symbol.typeRW;

        try
          {
            switch (scanner.lookahead(4).symbol())
              {
                case Symbol.arrayRW  -> parseArrayTypeDecl();
                case Symbol.recordRW -> parseRecordTypeDecl();
                case Symbol.lessThan -> parseEnumTypeDecl();
                default ->
                  {
                    Position errorPos = scanner.lookahead(4).position();
                    throw error(errorPos, "Invalid type declaration.");
                  }
              };
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            matchCurrentSymbol();   // force scanner past "type"
            recover(initialDeclFollowers());
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>arrayTypeDecl = "type" typeId "=" "array" "[" intConstValue "]"
     *                       "of" typeName ";" .</code>
     */
    private void parseArrayTypeDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>arrayTypeConstr = "array" "[" intConstValue "]" "of" typeName .</code>
     */
    private void parseArrayTypeConstr() throws IOException
      {
        try
          {
            match(Symbol.arrayRW);
            match(Symbol.leftBracket);
            parseConstValue();
            match(Symbol.rightBracket);
            match(Symbol.ofRW);
            parseTypeName();
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.assign, Symbol.semicolon));
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>recordTypeDecl = "type" typeId "=" "record" "{" fieldDecls "}" ";" .</code>
     */
    private void parseRecordTypeDecl() throws IOException
      {
        try
          {
            match(Symbol.typeRW);
            var typeId = scanner.token();
            match(Symbol.identifier);
            match(Symbol.equals);
            match(Symbol.recordRW);
            match(Symbol.leftBrace);

            idTable.openScope(ScopeLevel.RECORD);
            parseFieldDecls();
            idTable.closeScope();

            idTable.add(typeId, IdType.recordTypeId);
            match(Symbol.rightBrace);
            match(Symbol.semicolon);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initialDeclFollowers());
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>fieldDecls = { fieldDecl } .</code>
     */
    private void parseFieldDecls() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>fieldDecl = fieldId ":" typeName ";" .</code>
     */
    private void parseFieldDecl() throws IOException
      {
// ...  Hint: There are 2 symbols in the recover set for parseFieldDecl()
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>enumTypeDecl = "type" typeId "=" "<" identifiers ">" ";" .</code>
     */
    private void parseEnumTypeDecl() throws IOException
      {
        try
          {
            match(Symbol.typeRW);
            var typeId = scanner.token();
            match(Symbol.identifier);
            match(Symbol.equals);
            match(Symbol.lessThan);
            var identifiers = parseIdentifiers();   // might contain duplicates
            match(Symbol.greaterThan);
            match(Symbol.semicolon);

            // add type declaration to idTable
            idTable.add(typeId, IdType.enumTypeId);

            // Use a set to check for duplicates in the list of identifiers.
            var enumStrings = new HashSet<String>(identifiers.size());
            for (var identifier : identifiers)
              {
                if (!enumStrings.contains(identifier.text()))
                    enumStrings.add(identifier.text());
                else
                  {
                    // report an error identical to that of the identifier table
                    var errorMsg = "Identifier \"" + identifier.text()
                                 + "\" is already defined in the current scope.";
                    var e = error(identifier.position(), errorMsg);
                    errorHandler.reportError(e);
                  }
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initialDeclFollowers());
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>typeName = "Integer" | "Boolean" | "Byte"
     *                | "Char" | "String" | typeId .</code>
     */
    private void parseTypeName() throws IOException
      {
        try
          {
            switch (scanner.symbol())
              {
                case IntegerRW  -> matchCurrentSymbol();
                case BooleanRW  -> matchCurrentSymbol();
                case ByteRW     -> matchCurrentSymbol();
                case CharRW     -> matchCurrentSymbol();
                case StringRW   -> matchCurrentSymbol();
                case identifier ->
                  {
                    var typeId = scanner.token();
                    matchCurrentSymbol();

                    // use the declaration to determine the type
                    var type = idTable.get(typeId.text());

                    if (type != null)
                      {
                        if (   type == IdType.arrayTypeId
                            || type == IdType.recordTypeId
                            || type == IdType.enumTypeId)
                          {
                            ;   // empty statement for versions 1 and 2 of Parser
                          }
                        else
                          {
                            var errorMsg = "Identifier \"" + typeId.text()
                                         + "\" is not a valid type name.";
                            throw error(typeId.position(), errorMsg);
                          }
                      }
                    else
                      {
                        var errorMsg = "Identifier \"" + typeId.text()
                                      + "\" has not been declared as a type.";
                        throw error(typeId.position(), errorMsg);
                      }
                  }
                default -> throw error("Invalid type name.");
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.assign,    Symbol.semicolon, Symbol.comma,
                               Symbol.leftBrace, Symbol.rightParen));
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>subprogramDecls = { subprogramDecl } .</code>
     */
    private void parseSubprogramDecls() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>subprogramDecl = procedureDecl | functionDecl .</code>
     */
    private void parseSubprogramDecl() throws IOException
      {
// ...   throw an internal error if the symbol is not one of procRW or funRW
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>procedureDecl = "proc" procId "(" [ parameterDecls ] ")"
     *                       "{" initialDecls statements "}" .</code>
     */
    private void parseProcedureDecl() throws IOException
      {
        try
          {
            match(Symbol.procRW);
            var procId = scanner.token();
            match(Symbol.identifier);
            idTable.add(procId, IdType.procedureId);
            match(Symbol.leftParen);

            try
              {
                idTable.openScope(ScopeLevel.LOCAL);

                if (scanner.symbol().isParameterDeclStarter())
                    parseParameterDecls();

                match(Symbol.rightParen);
                match(Symbol.leftBrace);
                parseInitialDecls();
                parseStatements();
              }
            finally
              {
                idTable.closeScope();
              }

            match(Symbol.rightBrace);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(subprogDeclFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>functionDecl = "fun" funcId "(" [ parameterDecls ] ")" ":" typeName
     *                      "{" initialDecls statements "}" .</code>
     */
    private void parseFunctionDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>parameterDecls = parameterDecl { "," parameterDecl } .</code>
     */
    private void parseParameterDecls() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>parameterDecl = [ "var" ] paramId ":" typeName .</code>
     */
    private void parseParameterDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>statements = { statement } .</code>
     */
    private void parseStatements() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>statement = assignmentStmt | procedureCallStmt | compoundStmt | ifStmt
     *                 | loopStmt       | forLoopStmt       | exitStmt     | readStmt
     *                 | writeStmt      | writelnStmt       | returnStmt .</code>
     */
    private void parseStatement() throws IOException
      {
        try
          {
            if (scanner.symbol() == Symbol.identifier)
              {
                // Two possible cases: an assignment statement or a
                // function call.  Use lookahead tokens and declarations
                // to determine correct parsing action.
                var idStr = scanner.text();
                var idType = idTable.get(idStr);

                if (scanner.lookahead(2).symbol() == Symbol.leftParen)
                    parseProcedureCallStmt();
                else if (idType != null)
                  {
                    if (idType == IdType.variableId)
                        parseAssignmentStmt();
                    else
                        throw error("Identifier \"" + idStr + "\" cannot start a statement.");
                  }
                else
                    throw error("Identifier \"" + idStr + "\" has not been declared.");
              }
            else
              {
                switch (scanner.symbol())
                  {
                    case Symbol.leftBrace -> parseCompoundStmt();
                    case Symbol.ifRW      -> parseIfStmt();
// ...
                    default -> throw internalError(scanner.token()
                                   + " cannot start a statement.");
                  }
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);

            // Error recovery here is complicated for identifiers since they can both
            // start a statement and appear elsewhere in the statement.  (Consider,
            // for example, an assignment statement or a procedure call statement.)
            // Since the most common error is to declare or reference an identifier
            // incorrectly, we will assume that this is the case and advance to the
            // end of the current statement before performing error recovery.
            scanner.advanceTo(EnumSet.of(Symbol.semicolon, Symbol.rightBrace));
            recover(stmtFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>assignmentStmt = variable ":=" expression ";" .</code>
     */
    private void parseAssignmentStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>compoundStmt = "{" statements "}" .<\code>
     */
    private void parseCompoundStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>ifStmt = "if" booleanExpr "then" statement  [ "else" statement ] .</code>
     */
    private void parseIfStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>loopStmt = [ "while" booleanExpr ] "loop" statement .</code>
     */
    private void parseLoopStmt() throws IOException
      {
        try
          {
            if (scanner.symbol() == Symbol.whileRW)
              {
                matchCurrentSymbol();
                parseExpression();
              }

            match(Symbol.loopRW);
            parseStatement();
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(stmtFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>forLoopStmt = "for" varId "in" intExpr ".." intExpr "loop" statement .</code>
     */
    private void parseForLoopStmt() throws IOException
      {
        try
          {
            // create a new scope for the loop variable
            idTable.openScope(ScopeLevel.LOCAL);

            match(Symbol.forRW);
            var loopId = scanner.token();
            match(Symbol.identifier);
            idTable.add(loopId, IdType.variableId);
            match(Symbol.inRW);
            parseExpression();
            match(Symbol.dotdot);
            parseExpression();
            match(Symbol.loopRW);
            parseStatement();
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(stmtFollowers);
          }
        finally
          {
            idTable.closeScope();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>exitStmt = "exit" [ "when" booleanExpr ] ";" .</code>
     */
    private void parseExitStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>readStmt = "read" variable ";" .</code>
     */
    private void parseReadStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>writeStmt = "write" expressions ";" .</code>
     */
    private void parseWriteStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>expressions = expression { "," expression } .</code>
     */
    private void parseExpressions() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>writelnStmt = "writeln" [ expressions ] ";" .</code>
     */
    private void parseWritelnStmt() throws IOException
      {
        try
          {
            match(Symbol.writelnRW);

            if (scanner.symbol().isExprStarter())
                parseExpressions();

            match(Symbol.semicolon);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(stmtFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>procedureCallStmt = procId "(" [ actualParameters ] ")" ";" .
     *       actualParameters = expressions .</code>
     */
    private void parseProcedureCallStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>returnStmt = "return" [ expression ] ";" .</code>
     */
    private void parseReturnStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>variable = ( varId | paramId ) { indexExpr | fieldExpr } .<br>
     *       indexExpr = "[" expression "]" .<br>
     *       fieldExpr = "." fieldId .</code>
     * <br>
     * This helper method provides common logic for methods parseVariable() and
     * parseVariableExpr().  The method does not handle any ParserExceptions but
     * throws them back to the calling method where they can be handled appropriately.
     *
     * @throws ParserException if parsing fails.
     * @see #parseVariable()
     * @see #parseVariableExpr()
     */
    private void parseVariableCommon() throws IOException, ParserException
      {
        var idToken = scanner.token();
        match(Symbol.identifier);
        var idType = idTable.get(idToken.text());

        if (idType == null)
          {
            var errorMsg = "Identifier \"" + idToken.text()
                         + "\" has not been declared.";
            throw error(idToken.position(), errorMsg);
          }
        else if (idType != IdType.variableId)
          {
            var errorMsg = "Identifier \"" + idToken.text()
                       + "\" is not a variable.";
            throw error(idToken.position(), errorMsg);
          }

        while (scanner.symbol().isSelectorStarter())
          {
            if (scanner.symbol() == Symbol.leftBracket)
              {
                // parse index expression
                match(Symbol.leftBracket);
                parseExpression();
                match(Symbol.rightBracket);
              }
            else if (scanner.symbol() == Symbol.dot)
              {
                // parse field expression
                match(Symbol.dot);
                match(Symbol.identifier);
              }
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>variable = ( varId | paramId ) { indexExpr | fieldExpr } .</code>
     */
    private void parseVariable() throws IOException
      {
        try
          {
            parseVariableCommon();
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.assign, Symbol.semicolon));
          }
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>expression = relation { logicalOp relation }
     *                    [ "?" expression ":" expression ] .<br>
     *       logicalOp = "and" | "or" .</code>
     */
    private void parseExpression() throws IOException
      {
        try
          {
            parseRelation();

            while (scanner.symbol().isLogicalOperator())
              {
                matchCurrentSymbol();
                parseRelation();
              }

            if (scanner.symbol() == Symbol.questionMark)
              {
                matchCurrentSymbol();
                parseExpression();
                match(Symbol.colon);
                parseExpression();
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.semicolon, Symbol.colon,  Symbol.comma,
                               Symbol.thenRW,    Symbol.loopRW, Symbol.rightBracket,
                               Symbol.rightParen));
          }
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>relation = simpleExpr [ relationalOp simpleExpr ] .<br>
     *   relationalOp = "=" | "!=" | "&lt;" | "&lt;=" | "&gt;" | "&gt;=" .</code>
     */
    private void parseRelation() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>simpleExpr = [ signOp ] term { addingOp term } .<br>
     *       signOp = "+" | "-" .<br>
     *       addingOp = "+" | "-" .</code>
     */
    private void parseSimpleExpr() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>term = factor { multiplyingOp factor } .<br>
     *       multiplyingOp = "*" | "/" | "mod" | "&" | "<<" | ">>" .</code>
     */
    private void parseTerm() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>factor = ("not" | "~") factor | literal | constId | variableExpr
     *              | functionCallExpr | "(" expression ")" .</code>
     */
    private void parseFactor() throws IOException
      {
        try
          {
            var symbol = scanner.symbol();

            if (symbol == Symbol.notRW || symbol == Symbol.bitwiseNot)
              {
                matchCurrentSymbol();
                parseFactor();
              }
            else if (symbol.isLiteral())
              {
                // Handle constant literals separately from constant identifiers.
                parseConstValue();
              }
            else if (symbol == Symbol.identifier)
              {
                // Four possible cases: a declared constant, a variable
                // expression, a function call expression, or an enum
                // constant value.  Use lookahead tokens and declarations
                // to determine correct parsing action.
                var idStr  = scanner.text();
                var idType = idTable.get(idStr);

                if (scanner.lookahead(2).symbol() == Symbol.leftParen)
                    parseFunctionCallExpr();
                else if (idType != null)
                  {
                    if (idType == IdType.constantId)
                        parseConstValue();
                    else if (idType == IdType.variableId)
                        parseVariableExpr();
                    else if (idType == IdType.enumTypeId)
                        parseEnumConstValue();
                    else
                        throw error("Identifier \"" + idStr
                                  + "\" is not valid as an expression.");
                  }
                else
                    throw error("Identifier \"" + idStr + "\" has not been declared.");
              }
            else if (symbol == Symbol.leftParen)
              {
                matchCurrentSymbol();
                parseExpression();
                match(Symbol.rightParen);
              }
            else
                throw error("Invalid expression.");
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>constValue = ( [ "-" ] literal | constId ).</code>
     */
    private void parseConstValue() throws IOException
      {
        try
          {
            if (scanner.symbol().isLiteral())
                parseLiteral();
            else if (scanner.symbol() == Symbol.minus
                && scanner.lookahead(2).symbol() == Symbol.intLiteral)
              {
                // handle negative integer literals as a special case
                match(Symbol.minus);
                match(Symbol.intLiteral);
              }
            else if (scanner.symbol() == Symbol.identifier)
              {
                var constId = scanner.token();
                var idPosition = constId.position();
                matchCurrentSymbol();
                var idType = idTable.get(constId.text());

                if (idType == null)
                  {
                    var errorMsg = "Identifier \"" + constId.text()
                                 + "\" has not been declared.";
                    throw error(idPosition, errorMsg);
                  }
                else if (idType != IdType.constantId)
                  {
                    var errorMsg = "Identifier \"" + constId.text()
                                  + "\" was not declared as a constant.";
                    throw error(idPosition, errorMsg);
                  }
              }
            else
                throw error("Invalid constant.");
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.semicolon,  Symbol.comma,
                               Symbol.rightBrace, Symbol.rightBracket));
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>enumConstValue = enumTypeId "." enumConstId .</code>
     */
    private void parseEnumConstValue() throws IOException
      {
        try
          {
            var enumTypeId = scanner.token();
            match(Symbol.identifier);
            match(Symbol.dot);
            match(Symbol.identifier);

            var idType = idTable.get(enumTypeId.text());
            assert idType == IdType.enumTypeId;

            // Note that, for now, the parser does not check that the identifier
            // following Symbol.dot was actually declared as an enum constant in
            // the enum type.  We will fix this In version 3 of the parser.
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>variableExpr = variable .</code>
     */
    private void parseVariableExpr() throws IOException
      {
        try
          {
            parseVariableCommon();
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>functionCallExpr = funcId "(" [ actualParameters ] ")" .
     *       actualParameters = expressions .</code>
     */
    private void parseFunctionCallExpr() throws IOException
      {
// ...
      }

    // Utility parsing methods

    /**
     * Check that the current scanner symbol is the expected symbol.  If it
     * is, then advance the scanner.  Otherwise, throw a ParserException.
     */
    private void match(Symbol expectedSymbol) throws IOException, ParserException
      {
        if (scanner.symbol() == expectedSymbol)
            scanner.advance();
        else
          {
            var errorMsg = "Expecting \"" + expectedSymbol + "\" but found \""
                         + scanner.text() + "\" instead.";
            throw error(errorMsg);
          }
      }

    /**
     * Advance the scanner.  This method represents an unconditional match
     * with the current scanner symbol.
     */
    private void matchCurrentSymbol() throws IOException
      {
        scanner.advance();
      }

    /**
     * Advance the scanner until the current symbol is one of the
     * symbols in the specified set of follows.
     */
    private void recover(Set<Symbol> followers) throws IOException
      {
        scanner.advanceTo(followers);
      }

    /**
     * Create a parser exception with the specified error message and
     * the current scanner position.
     */
    private ParserException error(String errorMsg)
      {
        return error(scanner.position(), errorMsg);
      }

    /**
     * Create a parser exception with the specified error position and error message.
     */
    private ParserException error(Position errorPos, String errorMsg)
      {
        return new ParserException(errorPos, errorMsg);
      }

    /**
     * Create an internal compiler exception with the specified error
     * message and the current scanner position.
     */
    private InternalCompilerException internalError(String errorMsg)
      {
        return new InternalCompilerException(scanner.position(), errorMsg);
      }
  }
