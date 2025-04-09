package parser;

import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParserTest {
    @Test
    void testParsing() throws URISyntaxException {
        // Get the path of the test file
        String testFilePath = getResourceFilePath("Q7.txt");
        assertNotNull(testFilePath, "Test file not found!");

        // Run lexer
        LexicalAnalyzer lexer = new LexicalAnalyzer(testFilePath);
        List<Token> tokens = lexer.tokenize();

        // Run parser
        Parser parser = new Parser(tokens);
        assertNotNull(parser.parse());
    }

    // Helper method to get the file path of the test file
    private String getResourceFilePath(String fileName) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        return (resource != null) ? Paths.get(resource.toURI()).toString() : null;
    }
}
