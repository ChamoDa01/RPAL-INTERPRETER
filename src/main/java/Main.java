import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import parser.Parser;
import ast.AST;
import ast.StandardizedAST;
import cse.CSEMachine;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("1 argument: <file_path>");
            System.out.println("2 arguments: -ast/-st <file_path>");
            System.out.println("3 arguments: -ast -st <file_path>");
            return;
        }

        try {
            String filePath = args[args.length - 1];
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(filePath);
            List<Token> tokens = lexicalAnalyzer.tokenize();
            Parser parser = new Parser(tokens);
            AST ast = new AST(parser.parse());
            ast.standardize();
            StandardizedAST st = new StandardizedAST(ast.getRoot());
            CSEMachine cseMachine = new CSEMachine(st.getRoot());

            if (args.length == 1) {
                System.out.println(cseMachine.evaluate());
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "-ast":
                        ast.printAST();
                        break;
                    case "-st":
                        ast.printST();
                        break;
                    default:
                        System.out.println("Invalid argument: " + args[0]);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("-ast") && args[1].equalsIgnoreCase("-st")) {
                    ast.printAST();
                    ast.printST();
                }
                if (args[1].equalsIgnoreCase("-ast") && args[0].equalsIgnoreCase("-st")) {
                    ast.printAST();
                    ast.printST();
                } else {
                    System.out.println("Invalid combination of arguments.");
                }
            } else {
                System.out.println("Too many arguments provided.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred, please check your input.");
        }
    }
}