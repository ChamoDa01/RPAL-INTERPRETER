package parser;

public class Node {
    private String value;
    private NodeType type;
    private int childrenCount;

    public Node(NodeType type, String value, int childrenCount) {
        this.type = type;
        this.value = value;
        this.childrenCount = childrenCount;
    }

    public NodeType getType() {
        return type;
    }

    public String getValue(){
        return value;
    }

    public int getChildrenCount(){
        return childrenCount;
    }


}