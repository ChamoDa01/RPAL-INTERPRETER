package lexicalanalyzer;

public class Token {
    private TokenType tokenType;
    private String tokenValue;

    public Token(TokenType tokenType, String tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    public String toString() {
        return switch (tokenType) {
            case KEYWORD -> "<KEYWORD>: " + tokenValue;
            case IDENTIFIER -> "<IDENTIFIER>: " + tokenValue;
            case INTEGER -> "<INTEGER>: " + tokenValue;
            case OPERATOR -> "<OPERATOR>: " + tokenValue;
            case STRING -> "<STRING>: " + tokenValue;
            case PUNCTUATION -> "<PUNCTUATION>: " + tokenValue;
            case DELETE -> "<DELETE>: " + tokenValue;
            case END_OF_TOKENS -> "<END_OF_TOKENS>: " + tokenValue;
            default -> "<UNKNOWN>: " + tokenValue;
        };
    }

    public TokenType getTokenType(){
        return tokenType;
    }

    public String getTokenValue(){
        return tokenValue;
    }
}
