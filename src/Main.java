
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println(
                "Uso: java -cp src Main <arquivo.jack> [saida.xml]"
            );
            System.exit(1);
        }

        try {
            Path input = Paths.get(args[0]);
            Path output;

            if (args.length == 2) {
                output = Paths.get(args[1]);
            } else {
                String name = input.getFileName().toString();

                if (name.endsWith(".jack")) {
                    name = name.substring(0, name.length() - 5);
                }

                output = input.resolveSibling(name + "T.xml");
            }

            JackTokenizer tokenizer =
                    new JackTokenizer(input.toString());

            StringBuilder xml = new StringBuilder();
            xml.append("<tokens>\n");

            Token token;
            while ((token = tokenizer.nextToken()) != null) {
                xml.append(token.toXml()).append("\n");
            }

            xml.append("</tokens>\n");

            Path parent = output.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(
                output,
                xml.toString().getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("XML gerado: " + output);

        } catch (IOException | RuntimeException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }
}