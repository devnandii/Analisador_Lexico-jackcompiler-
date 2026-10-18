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

    // Guarda o token atual, que o Parser vai ler
    private Token currentToken;

    public JackTokenizer(String filePath) throws IOException {
        this.src = new String(
            Files.readAllBytes(Paths.get(filePath)),
            StandardCharsets.UTF_8
        );
        this.pos = 0;
        this.line = 1;
        // Carrega o primeiro token ao criar o objeto
        this.currentToken = readNextToken();
    }

    /**
     * Retorna o token atual sem avançar.
     */
    public Token peek() {
        return currentToken;
    }

    /**
     * Avança para o próximo token, descartando o atual.
     */
    public void advance() {
        currentToken = readNextToken();
    }

    /**
     * Retorna o tipo do token atual (ou EOF se for null).
     */
    public TokenType tokenType() {
        if (currentToken == null) return TokenType.EOF;
        return currentToken.type;
    }

    // ==========================================================
    // Leitura de baixo nível (lexer)
    // ==========================================================

    private Token readNextToken() {
        skipWhitespaceAndComments();

        if (pos >= src.length()) {
            return null; // Fim do arquivo
        }

        char c = src.charAt(pos);

        if (isDigit(c)) {
            return number();
        }

        // Correção do bug: '_' aceito como início
        if (isLetter(c) || c == '_') {
            return identifier();
        }

        if (c == '"') {
            return string();
        }

        if (SYMBOLS.contains(c)) {
            pos++;
            return new Token(TokenType.SYMBOL, String.valueOf(c), line);
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

        return new Token(TokenType.INT_CONST, lexeme, line);
    }

    private Token identifier() {
        int start = pos;
        pos++; // consome o primeiro caractere

        while (pos < src.length() && isIdentifierPart(src.charAt(pos))) {
            pos++;
        }

        String lexeme = src.substring(start, pos);

        TokenType type = KEYWORDS.contains(lexeme)
                ? TokenType.KEYWORD
                : TokenType.IDENTIFIER;

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

        return new Token(TokenType.STRING_CONST, lexeme, startLine);
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