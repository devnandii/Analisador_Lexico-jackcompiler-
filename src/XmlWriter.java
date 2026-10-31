import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmlWriter {
    private final StringBuilder xml;
    private int indentLevel;

    public XmlWriter() {
        this.xml = new StringBuilder();
        this.indentLevel = 0;
    }

    public void openTag(String tag) {
        appendIndent();
        xml.append("<").append(tag).append(">\n");
        indentLevel++;
    }

    public void closeTag(String tag) {
        indentLevel--;
        appendIndent();
        xml.append("</").append(tag).append(">\n");
    }

    public void writeToken(Token token) {
        appendIndent();
        xml.append(token.toXml()).append("\n");
    }

    public void saveToFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, xml.toString(), StandardCharsets.UTF_8);
    }

    private void appendIndent() {
        xml.append("  ".repeat(indentLevel));
    }
}