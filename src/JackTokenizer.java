import java.util.Set;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    private final String src;      // conteúdo do arquivo
    private int pos;               // posição atual
    private int line;              // linha atual (1-based)

    public JackTokenizer(String filePath) throws IOException {
        this.src = new String(Files.readAllBytes(Paths.get(filePath)));
        this.pos = 0;
        this.line = 1;
    }

    /** Retorna o próximo token ou null se acabou o arquivo. */
    public Token nextToken() {
        skipWhitespace();

        if (pos >= src.length()) {
            return null;
        }

        char c = src.charAt(pos);

        if (Character.isDigit(c)) {
            return number();
        }

        if (Character.isLetter(c) || c == '_') {
            return identifier();
        }

        if (c == '"') {
            return string();
        }
        
        if (SYMBOLS.contains(c)) {
            return symbol();
        }

        throw new RuntimeException(
            "Caractere inesperado '" + c + "' na linha " + line
        );
    }

    // ==========================================================
    // Reconhecedores
    // ==========================================================

    private Token number() {
        int start = pos;
        while (pos < src.length() && Character.isDigit(src.charAt(pos))) {
            pos++;
        }
        String lexeme = src.substring(start, pos);
        return new Token("integerConstant", lexeme, line);
    }

    private Token identifier() {
        int start = pos;
        while (pos < src.length()) {
            char c = src.charAt(pos);
            if (Character.isLetterOrDigit(c) || c == '_') {
                pos++;
            } else {
                break;
            }
        }
        String lexeme = src.substring(start, pos);

        String type = KEYWORDS.contains(lexeme) ? "keyword" : "identifier";
        return new Token(type, lexeme, line);
    }

    private Token symbol() {
        char c = src.charAt(pos);
        pos++;
        return new Token("symbol", String.valueOf(c), line);
    }

    private Token string() {
        pos++; // pula a aspas de abertura
        int start = pos;
        while (pos < src.length() && src.charAt(pos) != '"') {
            if (src.charAt(pos) == '\n') {
                throw new RuntimeException(
                    "String nao terminada na linha " + line
                );
            }
            pos++;
        }
        if (pos >= src.length()) {
            throw new RuntimeException(
                "String nao terminada (fim de arquivo) na linha " + line
            );
        }
        String lexeme = src.substring(start, pos);
        pos++; // pula a aspas de fechamento
        return new Token("stringConstant", lexeme, line);
}

    // ==========================================================
    // Utilidades
    // ==========================================================

    private void skipWhitespace() {
        while (pos < src.length()) {
            char c = src.charAt(pos);
            if (c == '\n') {
                line++;
                pos++;
            } else if (Character.isWhitespace(c)) {
                pos++;
            } else {
                break;
            }
        }
    }
}