package cse;

import Symbol.Symbol;
import ast.AST;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import org.junit.jupiter.api.Test;
import parser.Node;
import parser.Parser;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CSEMachineTest {
    @Test
    void testControlStructures() throws URISyntaxException {
        // Get the path of the test file
        String testFilePath = getResourceFilePath("Q2.txt");
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

        CSEMachine cseMachine = new CSEMachine(ast.getStandardizedRoot());
        Map<Integer, List<Symbol>> controlStructures = cseMachine.getControlStructures();

        // Print each ControlStructures
        for (Map.Entry<Integer, List<Symbol>> entry : controlStructures.entrySet()) {
            System.out.println("Control Structure " + entry.getKey() + ":");
            for (Symbol symbol : entry.getValue()) {
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }

    @Test
    void testEvaluate() throws URISyntaxException {
        // Get the path of the test file
        String testFilePath = getResourceFilePath("Test-4.txt");
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

        CSEMachine cseMachine = new CSEMachine(ast.getStandardizedRoot());
        Symbol result = cseMachine.evaluate();
        System.out.println(result);
    }

    // Helper method to get the file path of the test file
    private String getResourceFilePath(String fileName) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        return (resource != null) ? Paths.get(resource.toURI()).toString() : null;
    }
}
