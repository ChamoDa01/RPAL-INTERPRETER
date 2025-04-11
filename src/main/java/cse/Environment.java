package cse;

import Symbol.Symbol;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final int index;
    private final Environment parent;
    private final Map<String, Symbol> bindings;

    public Environment(int index) {
        this.index = index;
        this.parent = null;
        this.bindings = new HashMap<>();
    }

    public Environment(Environment parent, int index) {
        this.index = index;
        this.parent = parent;
        this.bindings = new HashMap<>();
    }

    public int getIndex() {
        return index;
    }

    public Environment getParent() {
        return parent;
    }

    public void bind(String name, Symbol value) {
        bindings.put(name, value);
    }

    public Symbol lookup(String name) {
        if (bindings.containsKey(name)) {
            return bindings.get(name);
        }

        if (parent != null) {
            return parent.lookup(name);
        }

        throw new RuntimeException("Unbound variable: " + name);
    }
}
