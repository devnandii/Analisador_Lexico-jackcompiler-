public class Token {
    public final TokenType type;
    public final String lexeme;
    public final int line;

    public Token(TokenType type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    public String toXml() {
        String tag;
        switch (type) {
            case KEYWORD:        tag = "keyword"; break;
            case SYMBOL:         tag = "symbol"; break;
            case IDENTIFIER:     tag = "identifier"; break;
            case INT_CONST:      tag = "integerConstant"; break;
            case STRING_CONST:   tag = "stringConstant"; break;
            default:             tag = "unknown"; break;
        }
        return "<" + tag + "> " + escapeXml(lexeme) + " </" + tag + ">";
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;"); // Adicionado o escape de aspas
    }
}