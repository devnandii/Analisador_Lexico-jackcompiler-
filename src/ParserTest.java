import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ParserTest {

    // Remover espaços no início das linhas
    private String normalizeXml(String content) {
        return content.lines()
                      .map(String::stripLeading)
                      .collect(Collectors.joining("\n"));
    }

    @Test
    public void testMainJack() throws Exception {
        // 1. Gera o XML do Main.jack
        JackTokenizer tokenizer = new JackTokenizer("test/jack_files/Main.jack");
        XmlWriter xml = new XmlWriter();
        CompilationEngine parser = new CompilationEngine(tokenizer, xml);
        parser.compileClass();
        
        String generatedPath = "test/generated/Main.xml";
        xml.saveToFile(generatedPath);

        // 2. Lê o gerado e o oficial
        String generated = normalizeXml(Files.readString(Paths.get(generatedPath)));
        String expected = normalizeXml(Files.readString(Paths.get("test/expected_xml/Main.xml")));

        // 3. Compara (o JUnit vai dizer se passou ou falhou)
        assertEquals(expected, generated, "A saída do Main.jack difere do oficial.");
    }

    @Test
    public void testSquareJack() throws Exception {
        JackTokenizer tokenizer = new JackTokenizer("test/jack_files/Square.jack");
        XmlWriter xml = new XmlWriter();
        CompilationEngine parser = new CompilationEngine(tokenizer, xml);
        parser.compileClass();
        
        String generatedPath = "test/generated/Square.xml";
        xml.saveToFile(generatedPath);

        String generated = normalizeXml(Files.readString(Paths.get(generatedPath)));
        String expected = normalizeXml(Files.readString(Paths.get("test/expected_xml/Square.xml")));

        assertEquals(expected, generated, "A saída do Square.jack difere do oficial.");
    }

    @Test
    public void testSquareGameJack() throws Exception {
        JackTokenizer tokenizer = new JackTokenizer("test/jack_files/SquareGame.jack");
        XmlWriter xml = new XmlWriter();
        CompilationEngine parser = new CompilationEngine(tokenizer, xml);
        parser.compileClass();
        
        String generatedPath = "test/generated/SquareGame.xml";
        xml.saveToFile(generatedPath);

        String generated = normalizeXml(Files.readString(Paths.get(generatedPath)));
        String expected = normalizeXml(Files.readString(Paths.get("test/expected_xml/SquareGame.xml")));

        assertEquals(expected, generated, "A saída do SquareGame.jack difere do oficial.");
    }
}