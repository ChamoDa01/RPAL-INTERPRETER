import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import parser.Node;
import parser.NodeType;
import parser.Parser;

import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("C:\\Users\\madhu\\Documents\\RPAL-INTERPRETER\\src\\test\\resources\\Q7.txt");
        List<Token> tokens = lexicalAnalyzer.tokenize();
        System.out.println(tokens);
        Parser parser = new Parser(tokens);
        parser.parse();
    }
}