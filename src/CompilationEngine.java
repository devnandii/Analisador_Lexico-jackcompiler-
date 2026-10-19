public class CompilationEngine {
    private final JackTokenizer tokenizer;
    private final XmlWriter xml;

    public CompilationEngine(JackTokenizer tokenizer, XmlWriter xml) {
        this.tokenizer = tokenizer;
        this.xml = xml;
    }

    // ==========================================================
    // Métodos Auxiliares
    // ==========================================================

    private boolean match(TokenType type) {
        return tokenizer.peek() != null && tokenizer.peek().type == type;
    }

    private boolean match(TokenType type, String value) {
        return match(type) && tokenizer.peek().lexeme.equals(value);
    }

    private void consume(TokenType type, String expected) {
        if (!match(type)) {
            String actual = tokenizer.peek() == null ? "EOF" : tokenizer.peek().lexeme;
            int line = tokenizer.peek() == null ? 0 : tokenizer.peek().line;
            throw new RuntimeException("Esperado " + expected + " mas encontrado " + actual + " na linha " + line);
        }
        xml.writeToken(tokenizer.peek());
        tokenizer.advance();
    }

    // ==========================================================
    // Regras da Gramática Jack
    // ==========================================================

    public void compileClass() {
        xml.openTag("class");
        consume(TokenType.KEYWORD, "class");
        consume(TokenType.IDENTIFIER, "className");
        consume(TokenType.SYMBOL, "{");

        while (match(TokenType.KEYWORD, "static") || match(TokenType.KEYWORD, "field")) {
            compileClassVarDec();
        }

        while (match(TokenType.KEYWORD, "constructor") || match(TokenType.KEYWORD, "function") || match(TokenType.KEYWORD, "method")) {
            compileSubroutine();
        }
        
        consume(TokenType.SYMBOL, "}");
        xml.closeTag("class");
    }

    public void compileClassVarDec() {
        xml.openTag("classVarDec");
        
        if (match(TokenType.KEYWORD, "static") || match(TokenType.KEYWORD, "field")) {
            consume(TokenType.KEYWORD, "keyword");
        } else {
            throw new RuntimeException("Esperado static ou field");
        }

        if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
            consume(TokenType.KEYWORD, "type");
        } else {
            consume(TokenType.IDENTIFIER, "type (className)");
        }

        consume(TokenType.IDENTIFIER, "varName");

        while (match(TokenType.SYMBOL, ",")) {
            consume(TokenType.SYMBOL, ",");
            consume(TokenType.IDENTIFIER, "varName");
        }

        consume(TokenType.SYMBOL, ";");
        xml.closeTag("classVarDec");
    }

    public void compileSubroutine() {
        xml.openTag("subroutineDec");

        if (match(TokenType.KEYWORD, "constructor") || match(TokenType.KEYWORD, "function") || match(TokenType.KEYWORD, "method")) {
            consume(TokenType.KEYWORD, "keyword");
        } else {
            throw new RuntimeException("Esperado constructor, function ou method");
        }

        if (match(TokenType.KEYWORD, "void") || match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
            consume(TokenType.KEYWORD, "returnType");
        } else {
            consume(TokenType.IDENTIFIER, "returnType (className)");
        }

        consume(TokenType.IDENTIFIER, "subroutineName");

        consume(TokenType.SYMBOL, "(");
        compileParameterList();
        consume(TokenType.SYMBOL, ")");

        compileSubroutineBody();

        xml.closeTag("subroutineDec");
    }

    public void compileParameterList() {
        xml.openTag("parameterList");
        
        if (!match(TokenType.SYMBOL, ")")) {
            if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
                consume(TokenType.KEYWORD, "type");
            } else {
                consume(TokenType.IDENTIFIER, "type (className)");
            }
            consume(TokenType.IDENTIFIER, "varName");

            while (match(TokenType.SYMBOL, ",")) {
                consume(TokenType.SYMBOL, ",");
                
                if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
                    consume(TokenType.KEYWORD, "type");
                } else {
                    consume(TokenType.IDENTIFIER, "type (className)");
                }
                consume(TokenType.IDENTIFIER, "varName");
            }
        }
        
        xml.closeTag("parameterList");
    }

    public void compileSubroutineBody() {
        xml.openTag("subroutineBody");
        consume(TokenType.SYMBOL, "{");

        while (match(TokenType.KEYWORD, "var")) {
            compileVarDec();
        }

        compileStatements();

        consume(TokenType.SYMBOL, "}");
        xml.closeTag("subroutineBody");
    }

    public void compileVarDec() {
        xml.openTag("varDec");
        
        consume(TokenType.KEYWORD, "var");

        if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
            consume(TokenType.KEYWORD, "type");
        } else {
            consume(TokenType.IDENTIFIER, "type (className)");
        }

        consume(TokenType.IDENTIFIER, "varName");

        while (match(TokenType.SYMBOL, ",")) {
            consume(TokenType.SYMBOL, ",");
            consume(TokenType.IDENTIFIER, "varName");
        }

        consume(TokenType.SYMBOL, ";");
        xml.closeTag("varDec");
    }

    public void compileStatements() {
        xml.openTag("statements");

        boolean hasStatement = true;
        while (hasStatement) {
            if (match(TokenType.KEYWORD, "let")) {
                compileLet();
            } else if (match(TokenType.KEYWORD, "if")) {
                compileIf();
            } else if (match(TokenType.KEYWORD, "while")) {
                compileWhile();
            } else if (match(TokenType.KEYWORD, "do")) {
                compileDo();
            } else if (match(TokenType.KEYWORD, "return")) {
                compileReturn();
            } else {
                hasStatement = false;
            }
        }

        xml.closeTag("statements");
    }

    public void compileLet() {
        xml.openTag("letStatement");
        consume(TokenType.KEYWORD, "let");
        consume(TokenType.IDENTIFIER, "varName");
        
        if (match(TokenType.SYMBOL, "[")) {
            consume(TokenType.SYMBOL, "[");
            compileExpression();
            consume(TokenType.SYMBOL, "]");
        }
        
        consume(TokenType.SYMBOL, "=");
        compileExpression();
        consume(TokenType.SYMBOL, ";");
        xml.closeTag("letStatement");
    }

    public void compileIf() {
        xml.openTag("ifStatement");
        consume(TokenType.KEYWORD, "if");
        consume(TokenType.SYMBOL, "(");
        compileExpression();
        consume(TokenType.SYMBOL, ")");
        consume(TokenType.SYMBOL, "{");
        compileStatements();
        consume(TokenType.SYMBOL, "}");
        
        if (match(TokenType.KEYWORD, "else")) {
            consume(TokenType.KEYWORD, "else");
            consume(TokenType.SYMBOL, "{");
            compileStatements();
            consume(TokenType.SYMBOL, "}");
        }
        xml.closeTag("ifStatement");
    }

    public void compileWhile() {
        xml.openTag("whileStatement");
        consume(TokenType.KEYWORD, "while");
        consume(TokenType.SYMBOL, "(");
        compileExpression();
        consume(TokenType.SYMBOL, ")");
        consume(TokenType.SYMBOL, "{");
        compileStatements();
        consume(TokenType.SYMBOL, "}");
        xml.closeTag("whileStatement");
    }

    public void compileDo() {
        xml.openTag("doStatement");
        consume(TokenType.KEYWORD, "do");
        compileSubroutineCall();
        consume(TokenType.SYMBOL, ";");
        xml.closeTag("doStatement");
    }

    public void compileReturn() {
        xml.openTag("returnStatement");
        consume(TokenType.KEYWORD, "return");
        
        if (!match(TokenType.SYMBOL, ";")) {
            compileExpression();
        }
        consume(TokenType.SYMBOL, ";");
        xml.closeTag("returnStatement");
    }

    public void compileExpression() {
        xml.openTag("expression");
        compileTerm();
        
        while (match(TokenType.SYMBOL, "+") || match(TokenType.SYMBOL, "-") ||
               match(TokenType.SYMBOL, "*") || match(TokenType.SYMBOL, "/") ||
               match(TokenType.SYMBOL, "&") || match(TokenType.SYMBOL, "|") ||
               match(TokenType.SYMBOL, "<") || match(TokenType.SYMBOL, ">") ||
               match(TokenType.SYMBOL, "=")) {
            
            consume(TokenType.SYMBOL, "op");
            compileTerm();
        }
        xml.closeTag("expression");
    }

    public void compileTerm() {
        xml.openTag("term");
        
        if (match(TokenType.INT_CONST) || match(TokenType.STRING_CONST)) {
            consume(tokenizer.peek().type, "const");
        } 
        else if (match(TokenType.KEYWORD, "true") || match(TokenType.KEYWORD, "false") || 
                 match(TokenType.KEYWORD, "null") || match(TokenType.KEYWORD, "this")) {
            consume(TokenType.KEYWORD, "keywordConstant");
        }
        else if (match(TokenType.SYMBOL, "(")) {
            consume(TokenType.SYMBOL, "(");
            compileExpression();
            consume(TokenType.SYMBOL, ")");
        }
        else if (match(TokenType.SYMBOL, "-") || match(TokenType.SYMBOL, "~")) {
            consume(TokenType.SYMBOL, "unaryOp");
            compileTerm();
        }
        else if (match(TokenType.IDENTIFIER)) {
            consume(TokenType.IDENTIFIER, "varName");
            
            if (match(TokenType.SYMBOL, "[")) {
                consume(TokenType.SYMBOL, "[");
                compileExpression();
                consume(TokenType.SYMBOL, "]");
            }
            else if (match(TokenType.SYMBOL, ".")) {
                consume(TokenType.SYMBOL, ".");
                consume(TokenType.IDENTIFIER, "subroutineName");
                consume(TokenType.SYMBOL, "(");
                compileExpressionList();
                consume(TokenType.SYMBOL, ")");
            }
            else if (match(TokenType.SYMBOL, "(")) {
                consume(TokenType.SYMBOL, "(");
                compileExpressionList();
                consume(TokenType.SYMBOL, ")");
            }
        } 
        else {
            throw new RuntimeException("Termo inesperado: " + tokenizer.peek().lexeme);
        }
        
        xml.closeTag("term");
    }

    public void compileSubroutineCall() {
        consume(TokenType.IDENTIFIER, "subroutineName");
        
        if (match(TokenType.SYMBOL, ".")) {
            consume(TokenType.SYMBOL, ".");
            consume(TokenType.IDENTIFIER, "subroutineName");
        }
        
        consume(TokenType.SYMBOL, "(");
        compileExpressionList();
        consume(TokenType.SYMBOL, ")");
    }

    public void compileExpressionList() {
        xml.openTag("expressionList");
        
        if (!match(TokenType.SYMBOL, ")")) {
            compileExpression();
            while (match(TokenType.SYMBOL, ",")) {
                consume(TokenType.SYMBOL, ",");
                compileExpression();
            }
        }
        
        xml.closeTag("expressionList");
    }
}