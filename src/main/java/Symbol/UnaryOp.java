package Symbol;

public class UnaryOp extends Symbol {
    private final String operator;

    public UnaryOp(String operator) {
        this.operator = operator;
    }

    public Symbol apply(Symbol arg) {
        if (arg instanceof IntValue && operator.equals("neg")) {
            return new IntValue(-((IntValue) arg).getValue());
        } else if (arg instanceof BoolValue && operator.equals("not")) {
            return new BoolValue(!((BoolValue) arg).getValue());
        }

        throw new RuntimeException("Invalid argument for operator: " + operator);
    }

    @Override
    public String toString() {
        return "UnaryOp(" + operator + ")";
    }
}