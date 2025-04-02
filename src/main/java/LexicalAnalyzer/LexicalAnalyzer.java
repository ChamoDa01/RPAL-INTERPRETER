package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Exception.LexicalException;

public class LexicalAnalyzer {
    private final String inputFile;
    private List<Token> tokenList;

    public LexicalAnalyzer(String inputFile) {
        this.inputFile = inputFile;
        this.tokenList = new ArrayList<>();
    }

    // Tokenize the input file
    public List<Token> tokenize() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int n = 0;
            while ((line = reader.readLine()) != null) {
                n++;
                try {
                    tokenizeLine(line);
                } catch (LexicalException e) {
                    //System.err.println("Error on line " + n + ": " + e.getMessage());
                    throw new RuntimeException("Error on line " + n + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            //System.err.println("Error reading file: " + e.getMessage());
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }
        return tokenList;
    }

    // Tokenize a single line of input
    private void tokenizeLine(String line) throws LexicalException {
        // Define basic character classes
        String digit = "[0-9]";
        String letter = "[a-zA-Z]";
        String operatorSymbol = "[+\\-*/<>&.@/:=~|$!#%^_\\[\\]{}\"`\\?]";
        // Define escape sequences for strings
        String escape = "(\\\\'|\\\\t|\\\\n|\\\\\\\\)";

        // Compile patterns for different token types
        Pattern identifierPattern = Pattern.compile(letter + "(" + letter + "|" + digit + "|" + "_)*");
        Pattern integerPattern = Pattern.compile(digit + "+");
        Pattern operatorPattern = Pattern.compile(operatorSymbol + "+");
        Pattern punctuationPattern = Pattern.compile("[(),;]");
        Pattern spacesPattern = Pattern.compile("(\\s|\\t)+");
        // Fixed string pattern to properly handle escape sequences and all valid characters
        Pattern stringPattern = Pattern.compile("'(([^'\\\\]|" + escape + ")*)'");
        // Pattern for comments
        Pattern commentPattern = Pattern.compile("//.*");

        int currentIndex = 0;
        while (currentIndex < line.length()) {
            char currentChar = line.charAt(currentIndex);
            String remainingLine = line.substring(currentIndex);

            // Skip spaces
            Matcher spaceMatcher = spacesPattern.matcher(remainingLine);
            if (spaceMatcher.lookingAt()) {
                String space = spaceMatcher.group();
                // Skip spaces without adding a token
                currentIndex += space.length();
                continue;
            }
            // Handle comments
            Matcher commentMatcher = commentPattern.matcher(remainingLine);
            if (commentMatcher.lookingAt()) {
                String comment = commentMatcher.group();
                // Skip comments without adding a token
                currentIndex += comment.length();
                continue;
            }
            // Check for identifiers
            Matcher identifierMatcher = identifierPattern.matcher(remainingLine);
            if (identifierMatcher.lookingAt()) {
                String identifier = identifierMatcher.group();

                // Check if the identifier is a keyword (including keywords)
                List<String> keywords = List.of(
                        "let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls",
                        "le", "eq", "ne", "true", "false", "nil", "dummy", "within", "and", "rec"
                );

                if (keywords.contains(identifier)) {
                    tokenList.add(new Token(TokenType.KEYWORD, identifier));
                } else {
                    tokenList.add(new Token(TokenType.IDENTIFIER, identifier));
                }

                currentIndex += identifier.length();
                continue;
            }
            // Check for integers
            Matcher integerMatcher = integerPattern.matcher(remainingLine);
            if (integerMatcher.lookingAt()) {
                String integer = integerMatcher.group();
                tokenList.add(new Token(TokenType.INTEGER, integer));
                currentIndex += integer.length();
                continue;
            }
            // Check for operators
            Matcher operatorMatcher = operatorPattern.matcher(remainingLine);
            if (operatorMatcher.lookingAt()) {
                String operator = operatorMatcher.group();
                tokenList.add(new Token(TokenType.OPERATOR, operator));
                currentIndex += operator.length();
                continue;
            }
            // Check for strings
            Matcher stringMatcher = stringPattern.matcher(remainingLine);
            if (stringMatcher.lookingAt()) {
                String string = stringMatcher.group();
                tokenList.add(new Token(TokenType.STRING, string));
                currentIndex += string.length();
                continue;
            }
            // Check for punctuation
            Matcher punctuationMatcher = punctuationPattern.matcher(Character.toString(currentChar));
            if (punctuationMatcher.matches()) {
                tokenList.add(new Token(TokenType.PUNCTUATION, Character.toString(currentChar)));
                currentIndex++;
                continue;
            }
            // If we reach here, the character doesn't match any token pattern
            throw new LexicalException("Unable to tokenize the CHARACTER: " + currentChar + " at INDEX: " + currentIndex);
        }
    }
}
