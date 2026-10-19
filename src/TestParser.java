import java.io.IOException;

public class TestParser {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Uso: java -cp src TestParser <arquivo.jack>");
            return;
        }
        
        String inputPath = args[0];
        String outputPath = inputPath.replace(".jack", "P.xml").replace("jack_files", "generated");

        JackTokenizer tokenizer = new JackTokenizer(inputPath);
        XmlWriter xml = new XmlWriter();
        
        CompilationEngine parser = new CompilationEngine(tokenizer, xml);
        parser.compileClass(); 
        
        xml.saveToFile(outputPath);
        System.out.println("Parser rodou até o fim sem erros! XML gerado em: " + outputPath);
    }
}