public class Token {
    public final String type;    // "identifier", etc.
    public final String lexeme;  // o texto reconhecido
    public final int line;       // linha onde o token começou

    public Token(String type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    @Override
    public String toString() {
        return "<" + type + "> " + lexeme + " </" + type + ">";
    }
}