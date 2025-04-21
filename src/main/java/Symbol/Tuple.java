package Symbol;

import java.util.List;

public class Tuple extends Symbol {
    private final List<Symbol> values;

    public Tuple(List<Symbol> values) {
        this.values = values;
    }

    public List<Symbol> getValues() {
        return values;
    }

    public Symbol get(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Tuple index out of bounds: " + index);
        }
        return values.get(index);
    }

    @Override
    public String toString() {
        return "Tuple" + values;
    }
}