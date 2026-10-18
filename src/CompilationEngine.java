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

        // Próximos passos (ficarão em loop):
        // compileClassVarDec();
        // compileSubroutineDec();

        consume(TokenType.SYMBOL, "}");
        xml.closeTag("class");
    }
}