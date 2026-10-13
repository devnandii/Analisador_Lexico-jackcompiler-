
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class JackTokenizer {

    private static final Set<String> KEYWORDS = Set.of(
        "class", "constructor", "function", "method", "field", "static",
        "var", "int", "char", "boolean", "void", "true", "false", "null",
        "this", "let", "do", "if", "else", "while", "return"
    );

    private static final Set<Character> SYMBOLS = Set.of(
        '{', '}', '(', ')', '[', ']', '.', ',', ';',
        '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
    );

    private final String src;
    private int pos;
    private int line;

    public JackTokenizer(String filePath) throws IOException {
        this.src = new String(
            Files.readAllBytes(Paths.get(filePath)),
            StandardCharsets.UTF_8
        );
        this.pos = 0;
        this.line = 1;
    }

    public Token nextToken() {
        skipWhitespaceAndComments();

        if (pos >= src.length()) {
            return null;
        }

        char c = src.charAt(pos);

        if (isDigit(c)) {
            return number();
        }

        if (isLetter(c)) {
            return identifier();
        }

        if (c == '"') {
            return string();
        }

        if (SYMBOLS.contains(c)) {
            pos++;
            return new Token("symbol", String.valueOf(c), line);
        }

        throw new RuntimeException(
            "Caractere inesperado '" + c + "' na linha " + line
        );
    }

    private Token number() {
        int start = pos;

        while (pos < src.length() && isDigit(src.charAt(pos))) {
            pos++;
        }

        String lexeme = src.substring(start, pos);

        try {
            long value = Long.parseLong(lexeme);

            if (value > 32767) {
                throw new RuntimeException(
                    "Inteiro fora do intervalo 0 a 32767 na linha "
                    + line + ": " + lexeme
                );
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                "Constante inteira invalida na linha " + line
            );
        }

        return new Token("integerConstant", lexeme, line);
    }

    private Token identifier() {
        int start = pos;
        pos++;

        while (pos < src.length() && isIdentifierPart(src.charAt(pos))) {
            pos++;
        }

        String lexeme = src.substring(start, pos);

        String type = KEYWORDS.contains(lexeme)
                ? "keyword"
                : "identifier";

        return new Token(type, lexeme, line);
    }

    private Token string() {
        int startLine = line;

        pos++; // Aspas de abertura
        int start = pos;

        while (pos < src.length() && src.charAt(pos) != '"') {
            char c = src.charAt(pos);

            if (c == '\n' || c == '\r') {
                throw new RuntimeException(
                    "String nao terminada na linha " + startLine
                );
            }

            pos++;
        }

        if (pos >= src.length()) {
            throw new RuntimeException(
                "String nao terminada no fim do arquivo, linha "
                + startLine
            );
        }

        String lexeme = src.substring(start, pos);
        pos++; // Aspas de fechamento

        return new Token("stringConstant", lexeme, startLine);
    }

    private void skipWhitespaceAndComments() {
        boolean skipping = true;

        while (skipping) {
            skipping = false;

            while (pos < src.length()
                    && Character.isWhitespace(src.charAt(pos))) {
                if (src.charAt(pos) == '\n') {
                    line++;
                }
                pos++;
            }

            if (pos + 1 < src.length()
                    && src.charAt(pos) == '/'
                    && src.charAt(pos + 1) == '/') {

                pos += 2;

                while (pos < src.length()
                        && src.charAt(pos) != '\n') {
                    pos++;
                }

                skipping = true;

            } else if (pos + 1 < src.length()
                    && src.charAt(pos) == '/'
                    && src.charAt(pos + 1) == '*') {

                pos += 2;
                boolean closed = false;

                while (pos + 1 < src.length()) {
                    if (src.charAt(pos) == '*'
                            && src.charAt(pos + 1) == '/') {
                        pos += 2;
                        closed = true;
                        break;
                    }

                    if (src.charAt(pos) == '\n') {
                        line++;
                    }

                    pos++;
                }

                if (!closed) {
                    throw new RuntimeException(
                        "Comentario de bloco nao fechado, linha " + line
                    );
                }

                skipping = true;
            }
        }
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentifierPart(char c) {
        return isLetter(c) || isDigit(c) || c == '_';
    }
}