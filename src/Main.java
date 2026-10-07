import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java -cp src Main <arquivo.jack>");
            System.exit(1);
        }

        try {
            JackTokenizer tok = new JackTokenizer(args[0]);
            Token t;
            while ((t = tok.nextToken()) != null) {
                System.out.println(t);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            System.exit(1);
        }
    }
}