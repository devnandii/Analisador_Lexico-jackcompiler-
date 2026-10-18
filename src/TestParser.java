import java.io.IOException;

public class TestParser {
    public static void main(String[] args) throws IOException {
        JackTokenizer tokenizer = new JackTokenizer("test/jack_files/Main.jack");
        XmlWriter xml = new XmlWriter();
        
        CompilationEngine parser = new CompilationEngine(tokenizer, xml);
        parser.compileClass(); 
        
        xml.saveToFile("test/generated/TestParser.xml");
        System.out.println("Parser rodou até o fim da classe sem erros!");
    }
}