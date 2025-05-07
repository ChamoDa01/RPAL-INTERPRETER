package Symbol;

public class Rator extends Symbol{
    private final String name;

    public Rator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //TODO: case "Isdummy" -> arg instanceof Dummy ? new BoolValue(true) : new BoolValue(false);
    public Symbol apply(Symbol arg) {
        return switch (name){
            case "Print","print" -> arg;
            case "Stem" -> stem(arg);
            case "Stern" -> stern(arg);
            case "Order" -> order(arg);
            case "Isinteger" -> arg instanceof IntValue ? new BoolValue(true) : new BoolValue(false);
            case "Isstring" -> arg instanceof StringValue ? new BoolValue(true) : new BoolValue(false);
            case "Istuple" -> arg instanceof Tuple ? new BoolValue(true) : new BoolValue(false);
            case "Istruthvalue" -> arg instanceof BoolValue ? new BoolValue(true) : new BoolValue(false);
            case "Isfunction" -> arg instanceof Delta ? new BoolValue(true) : new BoolValue(false);
            case "ItoS" -> itos(arg);
            case "Null" -> isNullTuple(arg);
            default -> throw new RuntimeException("Unknown rator: " + name);
        };
    }

    public Symbol apply(Symbol arg1, Symbol arg2) {
        if(arg1 instanceof StringValue str1 && arg2 instanceof StringValue str2){
            return new StringValue(str1.getValue() + str2.getValue());
        } else {
            throw new RuntimeException("Invalid arguments for " + name);
        }
    }

    private Symbol stem(Symbol arg){
        if(arg instanceof StringValue str){
            return new StringValue(str.getValue().substring(0,1));
        } else {
            throw new RuntimeException("Invalid argument for Stem: " + arg);
        }
    }

    private Symbol stern(Symbol arg){
        if(arg instanceof StringValue str){
            return new StringValue(str.getValue().substring(1));
        } else {
            throw new RuntimeException("Invalid argument for Stern: " + arg);
        }
    }

    private Symbol order(Symbol arg){
        if(arg instanceof Tuple tuple){
            return new IntValue(tuple.getValues().size());
        } else {
            throw new RuntimeException("Invalid argument for Order: " + arg);
        }
    }

    private Symbol itos(Symbol arg){
        if(arg instanceof IntValue intValue){
            return new StringValue(String.valueOf(intValue.getValue()));
        } else {
            throw new RuntimeException("Invalid argument for Itos: " + arg);
        }
    }

    private Symbol isNullTuple(Symbol arg){
        if(arg instanceof Tuple tuple){
            return new BoolValue(tuple.getValues().isEmpty());
        } else {
            throw new RuntimeException("Invalid argument for Null: " + arg);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
