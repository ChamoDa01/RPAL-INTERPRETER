package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Parser {
    private List<Token> tokenList;
    private List<Node> parseStack;

    public Parser(List<Token> tokenList) {
        this.tokenList = tokenList;
        parseStack = new ArrayList<>();
    }

    public Node parse() {
        tokenList.add(new Token(TokenType.END_OF_TOKENS, ""));
        E();
        if (tokenList.getFirst().getTokenType().equals(TokenType.END_OF_TOKENS)) {
            return parseStack.getFirst();
        } else {
            System.out.println("Parsing Unsuccessful!");
            throw new RuntimeException("Parsing Unsuccessful!");
        }
    }

    private void E() {

        int n = 0;
        Token token = tokenList.getFirst();
        if (token.getTokenType().equals(TokenType.KEYWORD)
                && Arrays.asList("let", "fn").contains(token.getTokenValue())) {
            if (token.getTokenValue().equals("let")) {
                tokenList.removeFirst();
                D();
                if (!tokenList.getFirst().getTokenValue().equals("in")) {
                    System.out.println("Parse error at E : 'in' Expected");
                }
                tokenList.removeFirst();
                E();
                build_tree(new Node(NodeType.LET, "let", 2));

            } else {
                tokenList.removeFirst();
                do {
                    Vb();
                    n++;
                } while (tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)
                        || tokenList.getFirst().getTokenValue().equals("("));
                if (!tokenList.getFirst().getTokenValue().equals(".")) {
                    System.out.println("Parse error at E : '.' Expected");
                }
                tokenList.removeFirst();
                E();
                build_tree(new Node(NodeType.LAMBDA, "lambda", n + 1));
            }
        } else
            Ew();
    }

    private void Ew() {
        T();
        if (tokenList.getFirst().getTokenValue().equals("where")) {
            tokenList.removeFirst();
            Dr();
            build_tree(new Node(NodeType.WHERE, "where", 2));
        }
    }

    private void T() {

        Ta();
        int n = 1;
        while (tokenList.getFirst().getTokenValue().equals(",")) {
            tokenList.removeFirst();
            Ta();
            ++n;
        }
        if (n > 1) {
            build_tree(new Node(NodeType.TAU, "tau", n));
        }
    }

    private void Ta() {
        Tc();
        while (tokenList.getFirst().getTokenValue().equals("aug")) {
            tokenList.removeFirst();
            Tc();
            build_tree(new Node(NodeType.AUG, "aug", 2));
        }
    }

    private void Tc() {
        B();
        if (tokenList.getFirst().getTokenValue().equals("->")) {
            tokenList.removeFirst();
            Tc();
            if (!tokenList.getFirst().getTokenValue().equals("|")) {
                System.out.println("Parse error at Tc: conditional '|' expected");
            }
            tokenList.removeFirst();
            Tc();
            build_tree(new Node(NodeType.CONDITION, "->", 3));
        }
    }

    private void B() {
        Bt();
        while (tokenList.getFirst().getTokenValue().equals("or")) {
            tokenList.removeFirst();
            Bt();
            build_tree(new Node(NodeType.OP_OR, "or", 2));
        }
    }

    private void Bt() {
        Bs();
        while (tokenList.getFirst().getTokenValue().equals("&")) {
            tokenList.removeFirst();
            Bs();
            build_tree(new Node(NodeType.OP_AND, "&", 2));
        }
    }

    private void Bs() {
        if (tokenList.getFirst().getTokenValue().equals("not")) {
            tokenList.removeFirst();
            Bp();
            build_tree(new Node(NodeType.OP_NOT, "not", 1));
        } else
            Bp();
    }

    private void Bp() {
        A();
        Token token = tokenList.getFirst();
        if (Arrays.asList(">", ">=", "<", "<=").contains(token.getTokenValue())
                || Arrays.asList("gr", "ge", "ls", "le", "eq", "ne").contains(token.getTokenValue())) {

            tokenList.removeFirst();
            A();
            switch (token.getTokenValue()) {
                case ">":
                    build_tree(new Node(NodeType.OP_COMPARE, "gr", 2));
                    break;
                case ">=":
                    build_tree(new Node(NodeType.OP_COMPARE, "ge", 2));
                    break;
                case "<":
                    build_tree(new Node(NodeType.OP_COMPARE, "ls", 2));
                    break;
                case "<=":
                    build_tree(new Node(NodeType.OP_COMPARE, "le", 2));
                    break;
                default:
                    build_tree(new Node(NodeType.OP_COMPARE, token.getTokenValue(), 2));
                    break;
            }
        }
    }

    private void A() {
        if (tokenList.getFirst().getTokenValue().equals("+")) {
            tokenList.removeFirst();
            At();
        } else if (tokenList.getFirst().getTokenValue().equals("-")) {
            tokenList.removeFirst();
            At();
            build_tree(new Node(NodeType.OP_NEGATION, "neg", 1));
        } else {
            At();
        }
        while (Arrays.asList("+", "-").contains(tokenList.getFirst().getTokenValue())) {
            Token currentToken = tokenList.getFirst();
            tokenList.removeFirst();
            At();
            if (currentToken.getTokenValue().equals("+"))
                build_tree(new Node(NodeType.OP_PLUS, "+", 2));
            else
                build_tree(new Node(NodeType.OP_MINUS, "-", 2));
        }

    }

    private void At() {
        Af();
        while (Arrays.asList("*", "/").contains(tokenList.getFirst().getTokenValue())) {
            Token currentToken = tokenList.getFirst();
            tokenList.removeFirst();
            Af();
            if (currentToken.getTokenValue().equals("*"))
                build_tree(new Node(NodeType.OP_MUL, "*", 2));
            else
                build_tree(new Node(NodeType.OP_DIV, "/", 2));
        }
    }

    private void Af() {
        Ap();
        if (tokenList.getFirst().getTokenValue().equals("**")) {
            tokenList.removeFirst();
            Af();
            build_tree(new Node(NodeType.OP_POW, "**", 2));
        }
    }

    private void Ap() {
        R();
        while (tokenList.getFirst().getTokenValue().equals("@")) {
            tokenList.removeFirst();

            if (!tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)) {
                System.out.println("Parsing error at Ap: IDENTIFIER EXPECTED");
            }
            build_tree(new Node(NodeType.IDENTIFIER, tokenList.getFirst().getTokenValue(), 0));
            tokenList.removeFirst();

            R();
            build_tree(new Node(NodeType.AT, "@", 3));
        }
    }

    private void R() {
        Rn();
        while ((Arrays.asList(TokenType.IDENTIFIER, TokenType.INTEGER, TokenType.STRING)
                .contains(tokenList.getFirst().getTokenType()))
                || (Arrays.asList("true", "false", "nil", "dummy").contains(tokenList.getFirst().getTokenValue()))
                || (tokenList.getFirst().getTokenValue().equals("("))) {

            Rn();
            build_tree(new Node(NodeType.GAMMA, "gamma", 2));
        }
    }

    private void Rn() {
        switch (tokenList.getFirst().getTokenType()) {
            case IDENTIFIER:
                build_tree(new Node(NodeType.IDENTIFIER, tokenList.getFirst().getTokenValue(), 0));
                tokenList.removeFirst();
                break;
            case INTEGER:
                build_tree(new Node(NodeType.INTEGER, tokenList.getFirst().getTokenValue(), 0));
                tokenList.removeFirst();
                break;
            case STRING:
                build_tree(new Node(NodeType.STRING, tokenList.getFirst().getTokenValue(), 0));
                tokenList.removeFirst();
                break;
            case KEYWORD:
                switch (tokenList.getFirst().getTokenValue()) {
                    case "true":
                        build_tree(new Node(NodeType.TRUE, tokenList.getFirst().getTokenValue(), 0));
                        tokenList.removeFirst();
                        break;
                    case "false":
                        build_tree(new Node(NodeType.FALSE, tokenList.getFirst().getTokenValue(), 0));
                        tokenList.removeFirst();
                        break;
                    case "nil":
                        build_tree(new Node(NodeType.NIL, tokenList.getFirst().getTokenValue(), 0));
                        tokenList.removeFirst();
                        break;
                    case "dummy":
                        build_tree(new Node(NodeType.DUMMY, tokenList.getFirst().getTokenValue(), 0));
                        tokenList.removeFirst();
                        break;
                    default:
                        System.out.println("Parse Error at Rn: Unexpected KEYWORD");
                        break;
                }
                break;
            case PUNCTUATION:
                if (tokenList.getFirst().getTokenValue().equals("(")) {
                    tokenList.removeFirst();

                    E();

                    if (!tokenList.getFirst().getTokenValue().equals(")")) {
                        System.out.println("Parsing error at Rn: Expected a matching ')'");
                    }
                    tokenList.removeFirst();
                } else
                    System.out.println("Parsing error at Rn: Unexpected PUNCTUATION");
                break;
            default:
                System.out.println("Parsing error at Rn: Expected a Rn, but got different");
                break;
        }
    }

    private void D() {
        Da();
        if (tokenList.getFirst().getTokenValue().equals("within")) {
            tokenList.removeFirst();
            D();
            build_tree(new Node(NodeType.WITHIN, "within", 2));
        }
    }

    private void Da() {
        Dr();
        int n = 1;
        while (tokenList.getFirst().getTokenValue().equals("and")) {
            tokenList.removeFirst();
            Dr();
            n++;
        }
        if (n > 1)
            build_tree(new Node(NodeType.AND, "and", n));
    }

    private void Dr() {
        boolean isRec = false;
        if (tokenList.getFirst().getTokenValue().equals("rec")) {
            tokenList.removeFirst();
            isRec = true;
        }
        Db();
        if (isRec) {
            build_tree(new Node(NodeType.REC, "rec", 1));
        }
    }

    private void Db() {
        if (tokenList.getFirst().getTokenType().equals(TokenType.PUNCTUATION)
                && tokenList.getFirst().getTokenValue().equals("(")) {
            tokenList.removeFirst();
            D();
            if (!tokenList.getFirst().getTokenValue().equals(")")) {
                System.out.println("Parsing error at Db #1");
            }
            tokenList.removeFirst();
        } else if (tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)) {
            if (tokenList.get(1).getTokenValue().equals("(")
                    || tokenList.get(1).getTokenType().equals(TokenType.IDENTIFIER)) { // Expect
                // a
                // fcn_form
                build_tree(new Node(NodeType.IDENTIFIER, tokenList.getFirst().getTokenValue(), 0));
                tokenList.removeFirst();
                int n = 1;
                do {
                    Vb();
                    n++;
                } while (tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)
                        || tokenList.getFirst().getTokenValue().equals("("));
                if (!tokenList.getFirst().getTokenValue().equals("=")) {
                    System.out.println("Parsing error at Db #2");
                }
                tokenList.removeFirst();
                E();

                build_tree(new Node(NodeType.FCN_FORM, "fcn_form", n + 1));

            } else if (tokenList.get(1).getTokenValue().equals("=")) {
                build_tree(new Node(NodeType.IDENTIFIER, tokenList.getFirst().getTokenValue(), 0));
                tokenList.removeFirst();
                tokenList.removeFirst();
                E();
                build_tree(new Node(NodeType.EQUAL, "=", 2));
            } else if (tokenList.get(1).getTokenValue().equals(",")) {
                Vl();
                if (!tokenList.getFirst().getTokenValue().equals("=")) {
                    System.out.println("Parsing error at Db");
                }
                tokenList.removeFirst();
                E();

                build_tree(new Node(NodeType.EQUAL, "=", 2));
            }
        }
    }

    private void Vb() {
        if (tokenList.getFirst().getTokenType().equals(TokenType.PUNCTUATION)
                && tokenList.getFirst().getTokenValue().equals("(")) {
            tokenList.removeFirst();
            boolean isVl = false;

            if (tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)) {
                Vl();
                isVl = true;
            }
            if (!tokenList.getFirst().getTokenValue().equals(")")) {
                System.out.println("Parse error unmatch )");
            }
            tokenList.removeFirst();
            if (!isVl)
                build_tree(new Node(NodeType.EMPTY_PARAMS, "()", 0));

        } else if (tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)) {
            build_tree(new Node(NodeType.IDENTIFIER, tokenList.getFirst().getTokenValue(), 0));
            tokenList.removeFirst();
        }
    }

    private void Vl() {
        int n = 0;
        do {
            if (n > 0) {
                tokenList.removeFirst();
            }
            if (!tokenList.getFirst().getTokenType().equals(TokenType.IDENTIFIER)) {
                System.out.println("Parse error: a ID was expected )");
            }
            build_tree(new Node(NodeType.IDENTIFIER, tokenList.getFirst().getTokenValue(), 0));
            tokenList.removeFirst();
            n++;
        }

        while (tokenList.getFirst().getTokenValue().equals(","));
        if (n > 1) {
            build_tree(new Node(NodeType.COMMA, ",", n));

        }
    }

    private void build_tree(Node node) {
        int childrenCount = node.getChildrenCount();
        List<Node> children = new ArrayList<>();
        for (int i = 0; i < childrenCount; i++) {
            children.addFirst(parseStack.removeLast()); // Pop from stack and add to children
        }
        node.setChildren(children); // Set the children to the node

        parseStack.add(node); // Add the parent node back to the stack
    }

    public void printPreOrderTraversal(Node root) {
        if (root == null) {
            return;
        }
        if (!parseStack.isEmpty()) {
            preOrderTraversal(root, "");
        }
    }

    private void preOrderTraversal(Node node, String level) {
        if (node == null) {
            return;
        }
        System.out.println(formatNode(level, node)); // Print the current node
        for (Node child : node.getChildren()) {
            preOrderTraversal(child, level + "."); // Recursively traverse the children
        }
    }

    String formatNode(String dots, Node node) {
        return switch (node.getType()) {
            case NodeType.IDENTIFIER -> dots + "<ID:" + node.getValue() + ">";
            case NodeType.INTEGER -> dots + "<INT:" + node.getValue() + ">";
            case NodeType.STRING -> dots + "<STR:" + node.getValue() + ">";
            case NodeType.TRUE, NodeType.DUMMY, NodeType.FALSE, NodeType.NIL -> dots + "<" + node.getValue() + ">";
            case NodeType.FCN_FORM -> dots + "function_form";
            default -> dots + node.getValue();
        };
    }
}