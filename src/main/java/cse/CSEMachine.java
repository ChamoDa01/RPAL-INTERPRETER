package cse;

import Symbol.*;
import parser.Node;
import parser.NodeType;

import java.util.*;

public class CSEMachine {
    private List<Symbol> control;
    private Stack<Symbol> stack;
    private Environment currentEnv;
    private Stack<Environment> environments;
    private Map<Integer, List<Symbol>> controlStructures; // Map of control structures by index
    private int nextControlIndex;
    private int envIndex;

    public CSEMachine(Node rootNode) {
        this.control = new ArrayList<>();
        this.stack = new Stack<>();
        this.currentEnv = new Environment(0);
        this.environments = new Stack<>();
        this.environments.push(currentEnv);
        this.controlStructures = new HashMap<>();
        this.nextControlIndex = 1; // Start with 1 since 0 is the main control
        this.envIndex = 1; // Start with 1 since 0 is the main environment

        // Create control structures from standardized tree
        createControlStructures(rootNode);
    }

    public Map<Integer, List<Symbol>> getControlStructures() {
        return controlStructures;
    }

    // Evaluate the program and return the result
    public Symbol evaluate() {
        // Initial configuration (rule 0)
        control.add(new EnvMarker(currentEnv));
        control.add(new Delta(0));
        stack.push(new EnvMarker(currentEnv));

        // Execute until control is empty
        while (!control.isEmpty()) {

            // TODO: remove after finished --------------------
            for (Symbol symbol : control) {
                System.out.print(symbol + " ");
            }
            System.out.println();
            for (Symbol symbol : stack){
                System.out.print(symbol + " ");
            }
            System.out.println("\n");
            // TODO: ------------------------------------------

            Symbol controlItem = control.removeLast();

            // Process based on type of control item
            if (controlItem instanceof Identifier ||
                    controlItem instanceof IntValue ||
                    controlItem instanceof StringValue ||
                    controlItem instanceof BoolValue ||
                    controlItem instanceof Ystar)
            {
                // Rule 1: Stack a name
                if (controlItem instanceof Identifier id) {
                    List<String> rators = List.of("Print", "Conc", "Stem", "Stern", "Order",
                            "Isinteger", "Isstring", "Istuple", "Isdummy", "Istruthvalue", "Isfunction",
                            "Itos", "Null");
                    if (rators.contains(id.getName())) {
                        // Handle built-in functions
                        stack.push(new Rator(id.getName()));
                    } else {
                        // Handle variable lookup
                        Symbol value = currentEnv.lookup(id.getName());
                        stack.push(value);
                    }
                } else {
                    stack.push(controlItem);
                }
            }
            else if (controlItem instanceof Lambda lambda) {
                // Rule 2: Stack a lambda
                Lambda closure = new Lambda(lambda.getBoundVars(), lambda.getControlIndex(), currentEnv);
                stack.push(closure);
            }
            else if (controlItem instanceof Gamma) {
                // Rules 3, 4, 10, 11, 12, 13
                Symbol value1 = stack.pop();
                Symbol value2 = stack.pop();

                switch (value1) {
                    case Rator rator -> {
                        // Rule 3: Built-in function application
                        // value2 = Rand

                        Symbol result;
                        if (rator.getName().equals("Conc")) {
                            // Handle Concatenation (required 2nd argument)
                            Symbol value3 = stack.pop();
                            result = rator.apply(value2, value3);
                        } else {
                            // Handle other built-in functions
                            result = rator.apply(value2);
                        }
                        stack.push(result);
                    }
                    case Lambda closure -> {
                        // Rule 4: Apply lambda-closure
                        // value2 = Rand

                        Environment newEnv = new Environment(closure.getEnvironment(), envIndex);
                        envIndex++;

                        if (closure.getBoundVars().size() == 1) {
                            // Handle single variable binding
                            newEnv.bind(closure.getBoundVars().getFirst(), value2);
                        } else if (value2 instanceof Tuple tuple && closure.getBoundVars().size() > 1) {
                            // Rule 11: Apply n-ary function
                            if (tuple.getValues().size() != closure.getBoundVars().size()) {
                                throw new RuntimeException("Tuple size doesn't match function parameters");
                            }

                            for (int i = 0; i < closure.getBoundVars().size(); i++) {
                                newEnv.bind(closure.getBoundVars().get(i), tuple.getValues().get(i));
                            }
                        }

                        // Push new environment
                        environments.push(newEnv);
                        currentEnv = newEnv;
                        control.add(new EnvMarker(newEnv));
                        control.addAll(controlStructures.get(closure.getControlIndex()));
                        stack.push(new EnvMarker(newEnv));
                    }
                    case Tuple tuple -> {
                        // Rule 10: Tuple selection
                        // value2 = Rand
                        IntValue rand = (IntValue) value2;
                        int index = rand.getValue();
                        Symbol value = tuple.get(index-1);
                        stack.push(value);
                    }
                    case Ystar ystar -> {
                        // Rule 12: Apply Y* (recursion operator)
                        // value2 = closure

                        Lambda closure = (Lambda) value2;
                        Eta eta = new Eta(closure.getBoundVars(), closure.getControlIndex(), closure.getEnvironment());
                        stack.push(eta);
                    }
                    case Eta eta -> {
                        // Rule 13: Applying functional part
                        // value2 = next Symbol

                        Lambda closure = new Lambda(eta.getBoundVars(), eta.getControlIndex(), eta.getEnvironment());
                        control.add(new Gamma());
                        control.add(new Gamma());
                        stack.push(value2);
                        stack.push(eta);
                        stack.push(closure);
                    }
                    case StringValue str -> {
                        // Conc built-in function
                        // Gamma Gamma    Conc 'Hello' 'World' (Handle by case Rator)
                        // Gamma          'Hello World'
                        // .              'Hello World'
                        stack.push(value2);
                        stack.push(value1);
                    }
                    default -> throw new RuntimeException("Invalid stackItem : " + value1);
                }
            }
            else if (controlItem instanceof EnvMarker controlEnv) {
                // Rule 5: Exit from environment
                Symbol value = stack.pop();
                EnvMarker stackEnv = (EnvMarker) stack.pop();
                if(controlEnv.getEnv().getIndex() != stackEnv.getEnv().getIndex()) {
                    throw new RuntimeException("Environment mismatch");
                }
                environments.pop();
                if(!environments.empty()) currentEnv = environments.peek();
                stack.push(value);
            }
            else if (controlItem instanceof BinaryOp op) {
                // Rule 6: Apply binary operator
                Symbol rand1 = stack.pop();
                Symbol rand2 = stack.pop();
                Symbol result = op.apply(rand1, rand2);
                stack.push(result);
            }
            else if (controlItem instanceof UnaryOp op) {
                // Rule 7: Apply unary operator
                Symbol rand = stack.pop();
                Symbol result = op.apply(rand);
                stack.push(result);
            }
            else if (controlItem instanceof Beta) {
                // Rule 8: Handle conditional
                BoolValue condition = (BoolValue) stack.pop();
                Delta elseStructure = (Delta) control.removeLast();
                Delta thenStructure = (Delta) control.removeLast();

                if (condition.getValue()) {
                    control.addAll(controlStructures.get(thenStructure.getIndex()));
                } else {
                    control.addAll(controlStructures.get(elseStructure.getIndex()));
                }
            }
            else if (controlItem instanceof Tau tau) {
                // Rule 9: Tuple formation
                List<Symbol> values = new ArrayList<>();
                for (int i = 0; i < tau.getSize(); i++) {
                    values.add(stack.pop());
                }
                stack.push(new Tuple(values));
            }
            else if (controlItem instanceof Delta delta) {
                // Handle control structure
                control.addAll(controlStructures.get(delta.getIndex()));
            }
        }

        // Result is on top of stack
        return stack.pop();
    }

    // Create control structures from standardized tree
    private void createControlStructures(Node root) {
        // Create main control structure (delta_0)
        List<Symbol> mainControl = new ArrayList<>();
        controlStructures.put(0, mainControl);

        createControlStructureRecursive(root, 0);
    }

    // Recursive helper for creating control structures
    private void createControlStructureRecursive(Node node, int currentControlIndex) {
        List<Symbol> currentControl = controlStructures.get(currentControlIndex);

        switch (node.getType()) {
            case NodeType.IDENTIFIER:
                currentControl.add(new Identifier(node.getValue()));
                break;

            case NodeType.INTEGER:
                currentControl.add(new IntValue(Integer.parseInt(node.getValue())));
                break;

            case NodeType.STRING:
                String stringValue = node.getValue();
                // Remove the first and last single quote if they exist
                if (stringValue.startsWith("'") && stringValue.endsWith("'")) {
                    stringValue = stringValue.substring(1, stringValue.length() - 1);
                }
                // Add the cleaned string value
                currentControl.add(new StringValue(stringValue));
                break;

            case NodeType.TRUE, NodeType.FALSE:
                currentControl.add(new BoolValue(Boolean.parseBoolean(node.getValue())));
                break;

            case GAMMA:
                currentControl.add(new Gamma());
                // Process children left to right
                for (int i = 0; i < node.getChildrenCount(); i++) {
                    createControlStructureRecursive(node.getChildren().get(i), currentControlIndex);
                }
                break;

            case LAMBDA:
                // Lambda's first child is bound variable, second is body
                List<String> boundVars = new ArrayList<>();
                processBoundVariables(node.getChildren().getFirst(), boundVars);

                int lambdaControlIndex = nextControlIndex++;
                currentControl.add(new Lambda(boundVars, lambdaControlIndex, null));

                // Create new control structure for lambda body
                List<Symbol> lambdaControl = new ArrayList<>();
                controlStructures.put(lambdaControlIndex, lambdaControl);

                // Process lambda body in new control structure
                createControlStructureRecursive(node.getChildren().get(1), lambdaControlIndex);
                break;

            case NodeType.CONDITION:
                // Create then and else control structures
                int thenControlIndex = nextControlIndex++;
                int elseControlIndex = nextControlIndex++;

                // Add structures in order: then, else, beta
                currentControl.add(new Delta(thenControlIndex, "then"));
                currentControl.add(new Delta(elseControlIndex, "else"));
                currentControl.add(new Beta());

                // Process condition
                createControlStructureRecursive(node.getChildren().getFirst(), currentControlIndex);

                // Create then and else control structures
                List<Symbol> thenControl = new ArrayList<>();
                List<Symbol> elseControl = new ArrayList<>();
                controlStructures.put(thenControlIndex, thenControl);
                controlStructures.put(elseControlIndex, elseControl);

                // Process then and else branches
                createControlStructureRecursive(node.getChildren().get(1), thenControlIndex);
                createControlStructureRecursive(node.getChildren().get(2), elseControlIndex);
                break;

            case NodeType.TAU:
                // Add tau operator
                int tupleSize = node.getChildrenCount();
                currentControl.add(new Tau(tupleSize));

                // Process tuple elements left to right
                for (int i = 0; i < tupleSize; i++) {
                    createControlStructureRecursive(node.getChildren().get(i), currentControlIndex);
                }
                break;

            case NodeType.OP_PLUS,
                 NodeType.OP_MINUS,
                 NodeType.OP_MUL,
                 NodeType.OP_DIV,
                 NodeType.OP_POW,
                 NodeType.OP_COMPARE,
                 NodeType.OP_AND,
                 NodeType.OP_OR,
                 NodeType.AUG:
                currentControl.add(new BinaryOp(node.getValue()));
                createControlStructureRecursive(node.getChildren().get(0), currentControlIndex);
                createControlStructureRecursive(node.getChildren().get(1), currentControlIndex);
                break;

            case NodeType.OP_NEGATION,
                 NodeType.OP_NOT:
                currentControl.add(new UnaryOp(node.getValue()));
                createControlStructureRecursive(node.getChildren().getFirst(), currentControlIndex);
                break;

            case NodeType.YSTAR:
                currentControl.add(new Ystar());
                break;

            case NodeType.DUMMY:
            case NodeType.EMPTY_PARAMS:
                break;
            case NodeType.NIL:
                currentControl.add(new StringValue("nil"));
                break;
            default:
                throw new RuntimeException("Unsupported node type: " + node.getType());
        }
    }

    // Process bound variables (handles both single variables and tuples)
    private void processBoundVariables(Node varNode, List<String> boundVars) {
        if (varNode.getType() == NodeType.IDENTIFIER) {
            // Single variable
            boundVars.add(varNode.getValue());
        } else if (varNode.getType() == NodeType.COMMA) {
            // Tuple of variables
            for (Node child : varNode.getChildren()) {
                if (child.getType() == NodeType.IDENTIFIER) {
                    boundVars.add(child.getValue());
                } else {
                    throw new RuntimeException("Invalid bound variable in tuple: " + child.getType());
                }
            }
        } else {
            throw new RuntimeException("Invalid bound variable: " + varNode.getType());
        }
    }
}