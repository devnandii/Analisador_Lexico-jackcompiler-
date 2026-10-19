public class CompilationEngine {
    private final JackTokenizer tokenizer;
    private final XmlWriter xml;

    public CompilationEngine(JackTokenizer tokenizer, XmlWriter xml) {
        this.tokenizer = tokenizer;
        this.xml = xml;
    }

    // Métodos auxiliares exigidos pelo professor
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

    // ==== REGRAS DA GRAMÁTICA ====

    public void compileClass() {
        xml.openTag("class");
        consume(TokenType.KEYWORD, "class");
        consume(TokenType.IDENTIFIER, "className");
        consume(TokenType.SYMBOL, "{");

        //ler as variáveis
        while (match(TokenType.KEYWORD, "static") || match(TokenType.KEYWORD, "field")) {
            compileClassVarDec();
        }
        while (match(TokenType.KEYWORD, "constructor") || match(TokenType.KEYWORD, "function") || match(TokenType.KEYWORD, "method")) {
            compileSubroutine();
        }
        consume(TokenType.SYMBOL, "}");
        xml.closeTag("class");
    

        // Próximo passo: compileSubroutineDec();
        
        consume(TokenType.SYMBOL, "}");
        xml.closeTag("class");
    }

    public void compileClassVarDec() {
        xml.openTag("classVarDec");
    
        // Pode ser 'static' ou 'field'
        if (match(TokenType.KEYWORD, "static") || match(TokenType.KEYWORD, "field")) {
            consume(TokenType.KEYWORD, "keyword");
        } else {
            throw new RuntimeException("Esperado static ou field");
        }

        // Tipo da variável (int, char, boolean ou className)
        if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
            consume(TokenType.KEYWORD, "type");
        } else {
            consume(TokenType.IDENTIFIER, "type (className)");
        }

    // Nome da variável
        consume(TokenType.IDENTIFIER, "varName");

    // Pode ter mais variáveis separadas por vírgula (ex: int x, y, z;)
        while (match(TokenType.SYMBOL, ",")) {
        consume(TokenType.SYMBOL, ",");
        consume(TokenType.IDENTIFIER, "varName");
        }

        consume(TokenType.SYMBOL, ";");
        xml.closeTag("classVarDec");
    }

    public void compileSubroutine() {
        xml.openTag("subroutineDec");

        // constructor | function | method
        if (match(TokenType.KEYWORD, "constructor") || match(TokenType.KEYWORD, "function") || match(TokenType.KEYWORD, "method")) {
            consume(TokenType.KEYWORD, "keyword");
        } else {
            throw new RuntimeException("Esperado constructor, function ou method");
        }

        // Tipo de retorno (void, int, boolean, char ou className)
        if (match(TokenType.KEYWORD, "void") || match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
            consume(TokenType.KEYWORD, "returnType");
        } else {
            consume(TokenType.IDENTIFIER, "returnType (className)");
        }

        // Nome da sub-rotina
        consume(TokenType.IDENTIFIER, "subroutineName");

        // Parâmetros
        consume(TokenType.SYMBOL, "(");
        compileParameterList();
        consume(TokenType.SYMBOL, ")");

        // Corpo (chaves e statements)
        compileSubroutineBody();

        xml.closeTag("subroutineDec");
    }

    public void compileParameterList() {
        xml.openTag("parameterList");
        
        // Se o próximo não for ')', significa que tem parâmetros
        if (!match(TokenType.SYMBOL, ")")) {
            // Tipo do parâmetro
            if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
                consume(TokenType.KEYWORD, "type");
            } else {
                consume(TokenType.IDENTIFIER, "type (className)");
            }
            // Nome do parâmetro
            consume(TokenType.IDENTIFIER, "varName");

            // Loop para os próximos parâmetros
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

        // Lê as variáveis locais
        while (match(TokenType.KEYWORD, "var")) {
            compileVarDec();
        }

        // Lê os comandos (statements)
        compileStatements();

        consume(TokenType.SYMBOL, "}");
        xml.closeTag("subroutineBody");
    }

    public void compileVarDec() {
        xml.openTag("varDec");
        
        consume(TokenType.KEYWORD, "var");

        // Tipo da variável (int, char, boolean ou className)
        if (match(TokenType.KEYWORD, "int") || match(TokenType.KEYWORD, "char") || match(TokenType.KEYWORD, "boolean")) {
            consume(TokenType.KEYWORD, "type");
        } else {
            consume(TokenType.IDENTIFIER, "type (className)");
        }

        // Nome da variável
        consume(TokenType.IDENTIFIER, "varName");

        // Pode ter mais variáveis separadas por vírgula (ex: var int x, y, z;)
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
                // Se não for nenhum dos comandos acima, significa que os statements acabaram (provavelmente achou o '}')
                hasStatement = false;
            }
        }

        xml.closeTag("statements");
    }

    public void compileLet() {
        xml.openTag("let");
        consume(TokenType.KEYWORD, "let");
        consume(TokenType.IDENTIFIER, "varName");
        
        // Array (ex: let a[1] = ...)
        if (match(TokenType.SYMBOL, "[")) {
            consume(TokenType.SYMBOL, "[");
            compileExpression();
            consume(TokenType.SYMBOL, "]");
        }
        
        consume(TokenType.SYMBOL, "=");
        compileExpression();
        consume(TokenType.SYMBOL, ";");
        xml.closeTag("let");
    }

    public void compileIf() {
        xml.openTag("if");
        consume(TokenType.KEYWORD, "if");
        consume(TokenType.SYMBOL, "(");
        compileExpression();
        consume(TokenType.SYMBOL, ")");
        consume(TokenType.SYMBOL, "{");
        compileStatements();
        consume(TokenType.SYMBOL, "}");
        
        // Else opcional
        if (match(TokenType.KEYWORD, "else")) {
            consume(TokenType.KEYWORD, "else");
            consume(TokenType.SYMBOL, "{");
            compileStatements();
            consume(TokenType.SYMBOL, "}");
        }
        xml.closeTag("if");
    }

    public void compileWhile() {
        xml.openTag("while");
        consume(TokenType.KEYWORD, "while");
        consume(TokenType.SYMBOL, "(");
        compileExpression();
        consume(TokenType.SYMBOL, ")");
        consume(TokenType.SYMBOL, "{");
        compileStatements();
        consume(TokenType.SYMBOL, "}");
        xml.closeTag("while");
    }

    public void compileDo() {
        xml.openTag("do");
        consume(TokenType.KEYWORD, "do");
        compileSubroutineCall(); // Substitui o compileExpression()
        consume(TokenType.SYMBOL, ";");
        xml.closeTag("do");
    }

    public void compileReturn() {
        xml.openTag("return");
        consume(TokenType.KEYWORD, "return");
        
        // Pode ter expressão (return x;) ou não (return;)
        if (!match(TokenType.SYMBOL, ";")) {
            compileExpression();
        }
        consume(TokenType.SYMBOL, ";");
        xml.closeTag("return");
    }

    public void compileExpression() {
        xml.openTag("expression");
        compileTerm();
        
        // Loop para operadores (+, -, *, /, &, |, <, >, =)
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
        
        // 1. Constantes
        if (match(TokenType.INT_CONST) || match(TokenType.STRING_CONST)) {
            consume(tokenizer.peek().type, "const");
        } 
        // 2. Constantes keyword (true, false, null, this)
        else if (match(TokenType.KEYWORD, "true") || match(TokenType.KEYWORD, "false") || 
                 match(TokenType.KEYWORD, "null") || match(TokenType.KEYWORD, "this")) {
            consume(TokenType.KEYWORD, "keywordConstant");
        }
        // 3. Parênteses (ex: (x + y))
        else if (match(TokenType.SYMBOL, "(")) {
            consume(TokenType.SYMBOL, "(");
            compileExpression();
            consume(TokenType.SYMBOL, ")");
        }
        // 4. Operador unário (-x ou ~x)
        else if (match(TokenType.SYMBOL, "-") || match(TokenType.SYMBOL, "~")) {
            consume(TokenType.SYMBOL, "unaryOp");
            compileTerm();
        }
        // 5. Variáveis, Arrays ou Chamadas de Função
        else if (match(TokenType.IDENTIFIER)) {
            consume(TokenType.IDENTIFIER, "varName");
            
            // Se tiver colchetes, é um array (ex: a[1])
            if (match(TokenType.SYMBOL, "[")) {
                consume(TokenType.SYMBOL, "[");
                compileExpression();
                consume(TokenType.SYMBOL, "]");
            }
            // Se tiver ponto, é chamada de sub-rotina (ex: SquareGame.new)
            else if (match(TokenType.SYMBOL, ".")) {
                consume(TokenType.SYMBOL, ".");
                consume(TokenType.IDENTIFIER, "subroutineName");
                consume(TokenType.SYMBOL, "(");
                compileExpressionList();
                consume(TokenType.SYMBOL, ")");
            }
            // Se tiver parênteses direto, é chamada de função local (ex: run())
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
        
        // Pode ser uma chamada de método de objeto (ex: game.run)
        if (match(TokenType.SYMBOL, ".")) {
            consume(TokenType.SYMBOL, ".");
            consume(TokenType.IDENTIFIER, "subroutineName");
        }
        
        // Parênteses da chamada (argumentos)
        consume(TokenType.SYMBOL, "(");
        compileExpressionList();
        consume(TokenType.SYMBOL, ")");
    }

    public void compileExpressionList() {
        xml.openTag("expressionList");
        
        // Se não for ')', significa que tem argumentos
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