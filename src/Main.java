import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Uso: java -cp src Main <arquivo.jack> [saida.xml]");
            System.exit(1);
        }

        try {
            Path input = Paths.get(args[0]);
            String outputPath;

            if (args.length == 2) {
                outputPath = args[1];
            } else {
                String name = input.getFileName().toString();
                if (name.endsWith(".jack")) {
                    name = name.substring(0, name.length() - 5);
                }
                outputPath = input.resolveSibling(name + ".xml").toString();
            }

            JackTokenizer tokenizer = new JackTokenizer(input.toString());
            XmlWriter xml = new XmlWriter();
            
            CompilationEngine parser = new CompilationEngine(tokenizer, xml);
            parser.compileClass(); 
            
            xml.saveToFile(outputPath);
            System.out.println("XML gerado com sucesso: " + outputPath);

        } catch (IOException | RuntimeException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }
}