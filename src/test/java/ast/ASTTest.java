package ast;

import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import org.junit.jupiter.api.Test;
import parser.Node;
import parser.Parser;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ASTTest {
    @Test
    void testAST() throws URISyntaxException{
        // Get the path of the test file
        String testFilePath = getResourceFilePath("conc.1");
        assertNotNull(testFilePath, "Test file not found!");

        // Run lexer
        LexicalAnalyzer lexer = new LexicalAnalyzer(testFilePath);
        List<Token> tokens = lexer.tokenize();

        // Run parser
        Parser parser = new Parser(tokens);
        Node root = parser.parse();

        // Get the AST root
        AST ast = new AST(root);
        ast.printAST();
    }

    @Test
    void testST() throws URISyntaxException{
        // Get the path of the test file
        String testFilePath = getResourceFilePath("conc.1");
        assertNotNull(testFilePath, "Test file not found!");

        // Run lexer
        LexicalAnalyzer lexer = new LexicalAnalyzer(testFilePath);
        List<Token> tokens = lexer.tokenize();

        // Run parser
        Parser parser = new Parser(tokens);
        Node root = parser.parse();

        // Get the AST root
        AST ast = new AST(root);
        ast.standardize();
        ast.printST();
    }

    // Helper method to get the file path of the test file
    private String getResourceFilePath(String fileName) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        return (resource != null) ? Paths.get(resource.toURI()).toString() : null;
    }
}
