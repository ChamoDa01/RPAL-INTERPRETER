package ast;

import parser.Node;
import parser.NodeType;

import java.util.ArrayList;
import java.util.List;

public class StandardizedAST {
    private Node root;

    public StandardizedAST(Node originalRoot) {
        this.root = standardize(originalRoot);
    }

    public Node getRoot() {
        return this.root;
    }

    public Node standardize(Node node) {
        if (node == null) {
            return null;
        }

        // First standardize all children (bottom-up approach)
        List<Node> standardizedChildren = new ArrayList<>();
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                standardizedChildren.add(standardize(child));
            }
            // Replace children with standardized versions
            node = createNewNodeWithChildren(node, standardizedChildren);
        }
        // Then apply transformation rules to the current node
        return transformNode(node);
    }


    private Node transformNode(Node node) {
        if (node == null) {
            return null;
        }

        return switch (node.getType()) {
            case NodeType.LET -> transformLet(node);
            case NodeType.WHERE -> transformWhere(node);
            case NodeType.FCN_FORM -> transformFcnForm(node);
            case NodeType.AT -> transformAt(node);
            case NodeType.WITHIN -> transformWithin(node);
            case NodeType.AND -> transformSimultdef(node);
            case NodeType.REC -> transformRec(node);
            case NodeType.LAMBDA -> transformLambda(node);
            default ->
                // Node types we do NOT standardize
                    node;
        };
    }

    private Node createNewNodeWithChildren(Node original, List<Node> newChildren) {
        Node newNode = new Node(original.getType(), original.getValue(), newChildren.size());
        newNode.setChildren(newChildren);
        return newNode;
    }

    /**
     * Transform LET node:
     *    LET         GAMMA
     *   /  \         /  \
     * EQUAL P -> LAMBDA  E
     *  / \        / \
     * X  E       X   P
     */
    private Node transformLet(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() != 2) {
            return node; // Not the expected structure
        }

        Node equalNode = children.get(0);
        Node p = children.get(1);

        if (equalNode.getType().name().equals("EQUAL") && equalNode.getChildren() != null && equalNode.getChildren().size() == 2) {
            Node x = equalNode.getChildren().get(0);
            Node e = equalNode.getChildren().get(1);

            // Create LAMBDA node
            List<Node> lambdaChildren = new ArrayList<>();
            lambdaChildren.add(x);
            lambdaChildren.add(p);
            Node lambdaNode = new Node(NodeType.LAMBDA,"lambda", lambdaChildren.size());
            lambdaNode.setChildren(lambdaChildren);

            // Create GAMMA node
            List<Node> gammaChildren = new ArrayList<>();
            gammaChildren.add(lambdaNode);
            gammaChildren.add(e);
            Node gammaNode = new Node(NodeType.GAMMA,"gamma", gammaChildren.size());
            gammaNode.setChildren(gammaChildren);

            return gammaNode;
        }

        return node; // Return unchanged if pattern doesn't match
    }

    /**
     * Transform WHERE node:
     *  WHERE         LET
     *  / \          /  \
     * P  EQUAL -> EQUAL P
     *    / \       / \
     *   X   E     X   E
     */
    private Node transformWhere(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() != 2) {
            return node; // Not the expected structure
        }

        Node p = children.get(0);
        Node equalNode = children.get(1);

        if (equalNode.getType().name().equals("EQUAL") && equalNode.getChildren() != null && equalNode.getChildren().size() == 2) {
            // Create LET node
            List<Node> letChildren = new ArrayList<>();
            letChildren.add(equalNode);
            letChildren.add(p);
            Node letNode = new Node(NodeType.LET,"let", letChildren.size());
            letNode.setChildren(letChildren);

            // Transform the resulting LET node
            return transformLet(letNode);
        }

        return node; // Return unchanged if pattern doesn't match
    }

    /**
     * Transform FCN_FORM node:
     * FCN_FORM      EQUAL
     *  / | \         /  \
     * P  V+ E  ->   P  +LAMBDA
     *                   / \
     *                  V  .E
     */
    private Node transformFcnForm(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() < 3) {
            return node; // Not the expected structure
        }

        Node p = children.getFirst();

        // Extract V+ (parameters)
        List<Node> vNodes = new ArrayList<>();
        for (int i = 1; i < children.size() - 1; i++) {
            vNodes.add(children.get(i));
        }
        Node e = children.getLast();

        // Create nested LAMBDA nodes for multiple parameters
        Node lambdaBody = e;
        for (int i = vNodes.size() - 1; i >= 0; i--) {
            List<Node> lambdaChildren = new ArrayList<>();
            lambdaChildren.add(vNodes.get(i));
            lambdaChildren.add(lambdaBody);

            Node lambdaNode = new Node(NodeType.LAMBDA,"lamda", lambdaChildren.size());
            lambdaNode.setChildren(lambdaChildren);
            lambdaBody = lambdaNode;
        }

        // Create EQUAL node
        List<Node> equalChildren = new ArrayList<>();
        equalChildren.add(p);
        equalChildren.add(lambdaBody);
        Node equalNode = new Node(NodeType.EQUAL,"=", equalChildren.size());
        equalNode.setChildren(equalChildren);

        return equalNode;
    }

    /**
     * Transform AT node:
     *    AT          GAMMA
     *  / | \  ->     / \
     * E1 N  E2    GAMMA E2
     *             / \
     *            N  E1
     */
    private Node transformAt(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() != 3) {
            return node; // Not the expected structure
        }

        Node e1 = children.get(0);
        Node n = children.get(1);
        Node e2 = children.get(2);

        // Create inner GAMMA node
        List<Node> innerGammaChildren = new ArrayList<>();
        innerGammaChildren.add(n);
        innerGammaChildren.add(e1);
        Node innerGammaNode = new Node(NodeType.GAMMA,"gamma", innerGammaChildren.size());
        innerGammaNode.setChildren(innerGammaChildren);

        // Create outer GAMMA node
        List<Node> outerGammaChildren = new ArrayList<>();
        outerGammaChildren.add(innerGammaNode);
        outerGammaChildren.add(e2);
        Node outerGammaNode = new Node(NodeType.GAMMA,"gamma", outerGammaChildren.size());
        outerGammaNode.setChildren(outerGammaChildren);

        return outerGammaNode;
    }

    /**
     * Transform WITHIN node:
     *    WITHIN          EQUAL
     *     /   \          / \
     * EQUAL  EQUAL ->  X2  GAMMA
     *  / \    / \           / \
     * X1 E1  X2 E2      LAMBDA E1
     *                    / \
     *                  X1  E2
     */
    private Node transformWithin(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() != 2) {
            return node; // Not the expected structure
        }

        Node equal1 = children.get(0);
        Node equal2 = children.get(1);

        if (equal1.getType().name().equals("EQUAL") && equal1.getChildren() != null && equal1.getChildren().size() == 2 &&
                equal2.getType().name().equals("EQUAL") && equal2.getChildren() != null && equal2.getChildren().size() == 2) {

            Node x1 = equal1.getChildren().get(0);
            Node e1 = equal1.getChildren().get(1);
            Node x2 = equal2.getChildren().get(0);
            Node e2 = equal2.getChildren().get(1);

            // Create LAMBDA node
            List<Node> lambdaChildren = new ArrayList<>();
            lambdaChildren.add(x1);
            lambdaChildren.add(e2);
            Node lambdaNode = new Node(NodeType.LAMBDA,"lamda", lambdaChildren.size());
            lambdaNode.setChildren(lambdaChildren);

            // Create GAMMA node
            List<Node> gammaChildren = new ArrayList<>();
            gammaChildren.add(lambdaNode);
            gammaChildren.add(e1);
            Node gammaNode = new Node(NodeType.GAMMA,"gamma", gammaChildren.size());
            gammaNode.setChildren(gammaChildren);

            // Create EQUAL node
            List<Node> equalChildren = new ArrayList<>();
            equalChildren.add(x2);
            equalChildren.add(gammaNode);
            Node equalNode = new Node(NodeType.EQUAL,"=", equalChildren.size());
            equalNode.setChildren(equalChildren);

            return equalNode;
        }

        return node; // Return unchanged if pattern doesn't match
    }

    /**
     * Transform SIMULTDEF node(and):
     *   and            EQUAL
     *    |             /  \
     * EQUAL++  ->  COMMA  TAU
     *  / \           |     |
     * X   E         X++   E++
     */
    private Node transformSimultdef(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return node; // Not the expected structure
        }

        List<Node> xNodes = new ArrayList<>();
        List<Node> eNodes = new ArrayList<>();

        // Extract X and E nodes from each EQUAL
        for (Node child : children) {
            if (child.getType().name().equals("EQUAL") && child.getChildren() != null && child.getChildren().size() == 2) {
                xNodes.add(child.getChildren().get(0));
                eNodes.add(child.getChildren().get(1));
            } else {
                return node; // Return unchanged if pattern doesn't match
            }
        }

        // Create COMMA node with X++
        Node commaNode = new Node(NodeType.COMMA,",", xNodes.size());
        commaNode.setChildren(xNodes);

        // Create TAU node with E++
        Node tauNode = new Node(NodeType.TAU,"tau", eNodes.size());
        tauNode.setChildren(eNodes);

        // Create EQUAL node
        List<Node> equalChildren = new ArrayList<>();
        equalChildren.add(commaNode);
        equalChildren.add(tauNode);
        Node equalNode = new Node(NodeType.EQUAL,"=", equalChildren.size());
        equalNode.setChildren(equalChildren);

        return equalNode;
    }

    /**
     * Transform REC node:
     *   REC       EQUAL
     *   |         /  \
     * EQUAL  ->  X   GAMMA
     *  / \            /  \
     * X   E        YSTAR  LAMBDA
     *                     / \
     *                    X   E
     */
    private Node transformRec(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() != 1) {
            return node; // Not the expected structure
        }

        Node equalNode = children.get(0);

        if (equalNode.getType().name().equals("EQUAL") && equalNode.getChildren() != null && equalNode.getChildren().size() == 2) {
            Node x = equalNode.getChildren().get(0);
            Node e = equalNode.getChildren().get(1);

            // Create LAMBDA node
            List<Node> lambdaChildren = new ArrayList<>();
            lambdaChildren.add(x);
            lambdaChildren.add(e);
            Node lambdaNode = new Node(NodeType.LAMBDA,"lamda", lambdaChildren.size());
            lambdaNode.setChildren(lambdaChildren);

            // Create YSTAR node (Y combinator)
            Node ystarNode = new Node(NodeType.YSTAR,"Y", 0);

            // Create GAMMA node
            List<Node> gammaChildren = new ArrayList<>();
            gammaChildren.add(ystarNode);
            gammaChildren.add(lambdaNode);
            Node gammaNode = new Node(NodeType.GAMMA,"gamma", gammaChildren.size());
            gammaNode.setChildren(gammaChildren);

            // Create EQUAL node
            List<Node> equalChildren = new ArrayList<>();
            equalChildren.add(x);
            equalChildren.add(gammaNode);
            Node newEqualNode = new Node(NodeType.EQUAL,"=", equalChildren.size());
            newEqualNode.setChildren(equalChildren);

            return newEqualNode;
        }

        return node; // Return unchanged if pattern doesn't match
    }

    /**
     * Transform LAMBDA node:
     *  LAMBDA        LAMBDA
     *   / \     ->    / \
     * V++  E         V  .E
     */
    private Node transformLambda(Node node) {
        List<Node> children = node.getChildren();
        if (children == null || children.size() < 2) {
            return node; // Not the expected structure
        }

        // Handle multiple parameters V++
        if (children.size() > 2) {
            List<Node> vNodes = new ArrayList<>();
            for (int i = 0; i < children.size() - 1; i++) {
                vNodes.add(children.get(i));
            }
            Node e = children.getLast();

            // Create nested LAMBDA nodes
            Node lambdaBody = e;
            for (int i = vNodes.size() - 1; i >= 0; i--) {
                List<Node> lambdaChildren = new ArrayList<>();
                lambdaChildren.add(vNodes.get(i));
                lambdaChildren.add(lambdaBody);

                Node lambdaNode = new Node(NodeType.LAMBDA,"lamda", lambdaChildren.size());
                lambdaNode.setChildren(lambdaChildren);
                lambdaBody = lambdaNode;
            }

            return lambdaBody;
        }

        // Already in standard form with one parameter
        return node;
    }
}