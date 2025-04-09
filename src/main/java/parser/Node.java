package parser;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String value;
    private final NodeType type;
    private final int childrenCount;
    private List<Node> children;

    public Node(NodeType type, String value, int childrenCount) {
        this.type = type;
        this.value = value;
        this.childrenCount = childrenCount;
        this.children = new ArrayList<>(childrenCount);
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

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        if (children.size() < childrenCount) {
            children.add(child);
        } else {
            throw new IllegalStateException("Cannot add more children to this node.");
        }
    }

    public void setChildren(List<Node> children) {
        if (children.size() == childrenCount) {
            this.children = children;
        } else {
            throw new IllegalArgumentException("Number of children does not match the expected count.");
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "value='" + value + '\'' +
                ", type=" + type +
                ", childrenCount=" + childrenCount +
                '}';
    }
}