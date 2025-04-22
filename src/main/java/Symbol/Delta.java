package Symbol;

public class Delta extends Symbol {
    private final int index;
    private final String type;

    public Delta(int index){
        this.index = index;
        this.type = null;
    }

    public Delta(int index, String type) {
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Delta(" + index + (type != null ? "," + type : "") + ")";
    }
}