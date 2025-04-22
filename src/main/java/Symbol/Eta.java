package Symbol;

import cse.Environment;

import java.util.List;

public class Eta extends Symbol {
    private final List<String> boundVars;
    private final int controlIndex;
    private final Environment environment;

    public Eta(List<String> boundVars, int controlIndex, Environment environment) {
        this.boundVars = boundVars;
        this.controlIndex = controlIndex;
        this.environment = environment;
    }

    public List<String> getBoundVars() {
        return boundVars;
    }

    public int getControlIndex() {
        return controlIndex;
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public String toString() {
        return "Eta(" +
                environment.getIndex() + "," +
                boundVars.getFirst() + "," +
                controlIndex + ')';
    }
}