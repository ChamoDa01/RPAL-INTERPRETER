package Symbol;

import cse.Environment;

public class EnvMarker extends Symbol {
    private final Environment env;

    public EnvMarker(Environment env) {
        this.env = env;
    }

    public Environment getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return "EnvMarker(" + env.getIndex() + ')';
    }
}