import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JackTokenizer {

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

        // Se cair aqui, é um caractere que ainda não reconhecemos.
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
        return new Token("identifier", lexeme, line);
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