package Symbol;

import cse.Environment;

import java.util.List;

public class Lambda extends Symbol {
    private final List<String> boundVars;
    private final int controlIndex;
    private final Environment environment;
//    private boolean isRecursive = false;
//    private Ystar ystar = null;
//    private Eta eta = null;
//    private Symbol specializedArg = null;

    public Lambda(List<String> boundVars, int controlIndex, Environment environment) {
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

//    public void setRecursive(boolean recursive) {
//        isRecursive = recursive;
//    }
//
//    public boolean isRecursive() {
//        return isRecursive;
//    }
//
//    public void setYstar(Ystar ystar) {
//        this.ystar = ystar;
//    }
//
//    public Ystar getYstar() {
//        return ystar;
//    }
//
//    public void setEta(Eta eta) {
//        this.eta = eta;
//    }
//
//    public Eta getEta() {
//        return eta;
//    }
//
//    public void setSpecializedArg(Symbol arg) {
//        this.specializedArg = arg;
//    }
//
//    public Symbol getSpecializedArg() {
//        return specializedArg;
//    }

    @Override
    public String toString() {
        return "Lambda(" +
                (environment != null ? environment.getIndex() + "," : "") +
                boundVars.getFirst() +
                "," + controlIndex +
                ')';
    }
}