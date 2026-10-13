
public class Token {
    public final String type;
    public final String lexeme;
    public final int line;

    public Token(String type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    public String toXml() {
        return "<" + type + "> " + escapeXml(lexeme)
                + " </" + type + ">";
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}