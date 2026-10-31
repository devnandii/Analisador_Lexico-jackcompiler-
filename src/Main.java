import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java -cp src Main <arquivo.jack> [saida.xml] [--lexer]");
            System.exit(1);
        }

        try {
            Path input = Paths.get(args[0]);
            String outputPath = args.length >= 2 && !args[1].equals("--lexer") ? args[1] : "output/saida.xml";
            boolean runLexerOnly = false;

            // Verifica se o modo Lexer foi pedido
            for (String arg : args) {
                if (arg.equals("--lexer")) runLexerOnly = true;
            }

            if (runLexerOnly) {
                // MODO LEXER: Gera o arquivo T.xml
                JackTokenizer tokenizer = new JackTokenizer(input.toString());
                XmlWriter xml = new XmlWriter();
                xml.openTag("tokens");
                while (tokenizer.peek() != null) {
                    xml.writeToken(tokenizer.peek());
                    tokenizer.advance();
                }
                xml.closeTag("tokens");
                xml.saveToFile(outputPath);
                System.out.println("XML de Tokens (Lexer) gerado com sucesso: " + outputPath);
            } else {
                // MODO PARSER: Gera o XML sintático
                JackTokenizer tokenizer = new JackTokenizer(input.toString());
                XmlWriter xml = new XmlWriter();
                CompilationEngine parser = new CompilationEngine(tokenizer, xml);
                parser.compileClass();
                xml.saveToFile(outputPath);
                System.out.println("XML do Parser gerado com sucesso: " + outputPath);
            }

        } catch (IOException | RuntimeException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }
}