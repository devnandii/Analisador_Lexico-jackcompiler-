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

        // Loop para ler todas as variáveis estáticas/fields
        while (match(TokenType.KEYWORD, "static") || match(TokenType.KEYWORD, "field")) {
            compileClassVarDec();
        }

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
}