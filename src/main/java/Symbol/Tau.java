package Symbol;

public class Tau extends Symbol {
    private final int size;

    public Tau(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Tau(" + size + ")";
    }
}