package Symbol;

import java.util.ArrayList;
import java.util.List;

public class BinaryOp extends Symbol {
    private final String operator;

    public BinaryOp(String operator) {
        this.operator = operator;
    }

    public Symbol apply(Symbol arg1, Symbol arg2) {
        if (arg1 instanceof IntValue && arg2 instanceof IntValue) {
            int val1 = ((IntValue) arg1).getValue();
            int val2 = ((IntValue) arg2).getValue();

            switch (operator) {
                case "+":
                    return new IntValue(val1 + val2);
                case "-":
                    return new IntValue(val1 - val2);
                case "*":
                    return new IntValue(val1 * val2);
                case "/":
                    return new IntValue(val1 / val2);
                case "**":
                    return new IntValue((int) Math.pow(val1, val2));
                case "gr":
                    return new BoolValue(val1 > val2);
                case "ge":
                    return new BoolValue(val1 >= val2);
                case "eq":
                    return new BoolValue(val1 == val2);
                case "ne":
                    return new BoolValue(val1 != val2);
                case "le":
                    return new BoolValue(val1 <= val2);
                case "ls":
                    return new BoolValue(val1 < val2);
            }
        } else if (operator.equals("eq") && arg1 instanceof StringValue str1 && arg2 instanceof StringValue str2) {
            // handle equality for String
            return new BoolValue(str1.getValue().equals(str2.getValue()));
        } else if (operator.equals("aug")) {
            if (arg1 instanceof Tuple tuple) {
                List<Symbol> newTuple = new ArrayList<>(tuple.getValues());
                newTuple.add(arg2);
                return new Tuple(newTuple);
            } else {
                // convert to tuple if it's a single value
                List<Symbol> newTuple = new ArrayList<>();
                newTuple.add(arg1);
                newTuple.add(arg2);
                return new Tuple(newTuple);
            }
        } else if (arg1 instanceof BoolValue bool1 && arg2 instanceof BoolValue bool2) {
            if(operator.equals("&")) {
                return new BoolValue(bool1.getValue() && bool2.getValue());
            } else if (operator.equals("or")) {
                return new BoolValue(bool1.getValue() || bool2.getValue());
            }
        }
        throw new RuntimeException("Invalid arguments for operator: " + operator);
    }

    @Override
    public String toString() {
        return "BinaryOp(" + operator + ")";
    }
}