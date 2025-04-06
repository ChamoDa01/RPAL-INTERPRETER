import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import parser.Node;
import parser.NodeType;

import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("C:\\Users\\madhu\\Documents\\RPAL-INTERPRETER\\src\\test\\resources\\Q1.txt");
        List<Token> tokens = lexicalAnalyzer.tokenize();
        Iterator<Token> tokenList = tokens.iterator();
        Token token = tokenList.next();
        while (token.getTokenType().equals(TokenType.KEYWORD) && token.getTokenValue().equals("let")) {
            System.out.println(token);
            tokenList.remove();
            token = tokenList.next();
            if (token.getTokenType().equals(TokenType.KEYWORD) && token.getTokenValue().equals("in")){
                System.out.println(token);
                tokenList.remove();
            }else{
                System.out.println("Parse error at E");
            }

            System.out.println(token);
        }
    }
}