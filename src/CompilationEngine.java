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
        consume(TokenType.SYMBOL, "}");
        xml.closeTag("subroutineBody");
    }
}