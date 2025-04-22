package Symbol;

import java.util.ArrayList;
import java.util.List;

public class BinaryOp extends Symbol {
    private final String operator;

    public BinaryOp(String operator) {
        this.operator = operator;
    }

    public Symbol apply(final Symbol arg1, final Symbol arg2) {
        if (arg1 instanceof IntValue int1 && arg2 instanceof IntValue int2) {
            int val1 = int1.getValue();
            int val2 = int2.getValue();

            return switch (operator) {
                case "+" -> new IntValue(val1 + val2);
                case "-" -> new IntValue(val1 - val2);
                case "*" -> new IntValue(val1 * val2);
                case "/" -> new IntValue(val1 / val2);
                case "**" -> new IntValue((int) Math.pow(val1, val2));
                case "gr" -> new BoolValue(val1 > val2);
                case "ge" -> new BoolValue(val1 >= val2);
                case "eq" -> new BoolValue(val1 == val2);
                case "ne" -> new BoolValue(val1 != val2);
                case "le" -> new BoolValue(val1 <= val2);
                case "ls" -> new BoolValue(val1 < val2);
                default -> throw new RuntimeException("Invalid argument types for operator (" + operator + "): " + arg1 + ", " + arg2);
            };
        }

        if (arg1 instanceof StringValue str1 && arg2 instanceof StringValue str2) {
            String val1 = str1.getValue();
            String val2 = str2.getValue();

            return switch (operator) {
                case "eq" -> new BoolValue(val1.equals(val2));
                case "ne" -> new BoolValue(!val1.equals(val2));
                case "aug" -> {
                    List<Symbol> tuple = new ArrayList<>();
                    if ("nil".equals(val1)) {
                        tuple.add(str2);
                    } else {
                        tuple.add(str1);
                        tuple.add(str2);
                    }
                    yield new Tuple(tuple);
                }
                default -> throw new RuntimeException("Invalid argument types for operator (" + operator + "): " + arg1 + ", " + arg2);
            };
        }

        if ("aug".equals(operator)) {
            List<Symbol> tuple = new ArrayList<>();
            if (arg1 instanceof Tuple t) {
                tuple.addAll(t.getValues());
                tuple.add(arg2);
                return new Tuple(tuple);
            }
            if (arg1 instanceof StringValue s && "nil".equals(s.getValue())) {
                tuple.add(arg2);
                return new Tuple(tuple);
            }
            tuple.add(arg1);
            tuple.add(arg2);
            return new Tuple(tuple);
        }

        if (arg1 instanceof BoolValue b1 && arg2 instanceof BoolValue b2) {
            return switch (operator) {
                case "&" -> new BoolValue(b1.getValue() && b2.getValue());
                case "or" -> new BoolValue(b1.getValue() || b2.getValue());
                default -> throw new RuntimeException("Invalid argument types for operator (" + operator + "): " + arg1 + ", " + arg2);
            };
        }

        throw new RuntimeException("Invalid argument types for operator (" + operator + "): " + arg1 + ", " + arg2);
    }

    @Override
    public String toString() {
        return "BinaryOp(" + operator + ")";
    }
}