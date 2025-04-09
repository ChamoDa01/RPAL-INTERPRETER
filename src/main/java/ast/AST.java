package ast;

import parser.Node;
import parser.NodeType;

public class AST {
    private Node astRoot;
    private StandardizedAST standardizedAST;

    public AST(Node root) {
        this.astRoot = root;
    }

    public Node getRoot() {
        return astRoot;
    }

    public Node getStandardizedRoot() {
        if (standardizedAST == null) {
            standardizedAST = new StandardizedAST(astRoot);
        }
        return standardizedAST.getRoot();
    }

    public boolean isStandardized() {
        return standardizedAST != null;
    }

    public void standardize() {
        if (standardizedAST == null) {
            standardizedAST = new StandardizedAST(astRoot);
        }
    }

    public void printAST() {
        preOrderTraverse(astRoot, "");
    }

    public void printST(){
        if(standardizedAST == null){
            System.out.println("AST is not standardized yet.");
            return;
        }
        preOrderTraverse(standardizedAST.getRoot(), "");
    }

    private void preOrderTraverse(Node node, String level) {
        if (node == null) {
            return;
        }
        System.out.println(formatNode(level, node)); // Print the current node
        for (Node child : node.getChildren()) {
            preOrderTraverse(child, level + "."); // Recursively traverse the children
        }
    }

    String formatNode(String dots, Node node) {
        return switch (node.getType()) {
            case NodeType.IDENTIFIER -> dots + "<ID:" + node.getValue() + ">";
            case NodeType.INTEGER -> dots + "<INT:" + node.getValue() + ">";
            case NodeType.STRING -> dots + "<STR:" + node.getValue() + ">";
            case NodeType.TRUE, NodeType.DUMMY, NodeType.FALSE, NodeType.NIL -> dots + "<" + node.getValue() + ">";
            case NodeType.FCN_FORM -> dots + "function_form";
            case NodeType.YSTAR -> dots + "<Y*>";
            default -> dots + node.getValue();
        };
    }
}