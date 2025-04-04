import lexicalanalyzer.LexicalAnalyzer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("C:\\Users\\madhu\\Documents\\RPAL-INTERPRETER\\src\\test\\resources\\Q1.txt");
        System.out.println(lexicalAnalyzer.tokenize());
    }
}