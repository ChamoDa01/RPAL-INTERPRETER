package Symbol;

public class BoolValue extends Symbol {
    private final boolean value;

    public BoolValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoolValue) {
            return value == ((BoolValue) obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}