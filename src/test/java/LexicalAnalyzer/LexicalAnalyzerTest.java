package LexicalAnalyzer;

import org.junit.jupiter.api.Test;

import java.util.List;

public class LexicalAnalyzerTest {
    @Test
    void testTokenization() {
        LexicalAnalyzer lexer = new LexicalAnalyzer("testInput.txt");
        List<Token> tokens = lexer.tokenize();
        for(Token token : tokens) {
            System.out.println(token.toString());
        }
        System.out.println("Successfully tokenized " + tokens.size() + " tokens.");
    }
}
