package Parser;

public class Node {
    private NodeType type;
    private String value;
    private int numChildren; // Number of children this node can vary ex: tau

    public Node(NodeType type, String value, int numChildren) {
        this.type = type;
        this.value = value;
        this.numChildren = numChildren;
    }
 
}
