package lexicalanalyzer;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LexicalAnalyzerTest {
    @Test
    void testTokenization() throws URISyntaxException {
        // Get the path of the test file
        String testFilePath = getResourceFilePath("Test-5.txt");
        assertNotNull(testFilePath, "Test file not found!");

        // Run lexer
        LexicalAnalyzer lexer = new LexicalAnalyzer(testFilePath);
        List<Token> tokens = lexer.tokenize();

        // Ensure we got some tokens
        assertNotNull(tokens, "Token list should not be null");
        assertFalse(tokens.isEmpty(), "Token list should not be empty");

        // Print tokens only if the test passes
        tokens.forEach(System.out::println);
        System.out.println("Successfully tokenized " + tokens.size() + " tokens.");
    }

    // Helper method to get the file path of the test file
    private String getResourceFilePath(String fileName) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        return (resource != null) ? Paths.get(resource.toURI()).toString() : null;
    }

}
