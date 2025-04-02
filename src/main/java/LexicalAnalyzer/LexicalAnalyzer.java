package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Exception.LexicalException;

public class LexicalAnalyzer {
    private final String inputFile;
    private List<Token> tokenList;

    public LexicalAnalyzer(String inputFile) {
        this.inputFile = inputFile;
        this.tokenList = new ArrayList<>();
    }

    public List<Token> tokenize() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int n = 0;
            while ((line = reader.readLine()) != null) {
                n++;
                try {
                    tokenizeLine(line);
                } catch (LexicalException e) {
                    System.err.println("Error on line " + n + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return tokenList;
    }

    private void tokenizeLine(String line) throws LexicalException {

    }
}
