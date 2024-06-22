import java.util.Scanner;
import java.util.Stack;

public class Calculator {

    public static void main(String[] args) {
        // Create a scanner object to read user input
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a mathematical expression: ");
            String expression = scanner.nextLine();

            try {
                // Evaluate the expression entered by the user
                double result = evaluateExpression(expression);
                System.out.println("The result is: " + result);
            } catch (Exception e) {
                System.out.println("Invalid expression: " + e.getMessage());
            }
        }
    }

    // Method to evaluate a mathematical expression
    private static double evaluateExpression(String expression) throws Exception {
        return evaluatePostfix(infixToPostfix(expression));
    }

    // Method to convert infix expression to postfix expression
    private static String infixToPostfix(String expression) throws Exception {
        Stack<Character> operatorStack = new Stack<>();
        StringBuilder postfix = new StringBuilder();
        char[] tokens = expression.toCharArray();

        for (char token : tokens) {
            if (Character.isDigit(token) || token == '.') {
                // Append digit or decimal point directly to postfix expression
                postfix.append(token);
            } else if (token == '(') {
                // Push '(' to the stack
                operatorStack.push(token);
            } else if (token == ')') {
                // Pop from stack to postfix expression until '(' is encountered
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    postfix.append(' ').append(operatorStack.pop());
                }
                if (operatorStack.isEmpty() || operatorStack.pop() != '(') {
                    throw new Exception("Mismatched parentheses");
                }
            } else if (isOperator(token)) {
                // Append a space before operator
                postfix.append(' ');
                // Pop operators from stack to postfix expression based on precedence
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                    postfix.append(operatorStack.pop()).append(' ');
                }
                // Push current operator to stack
                operatorStack.push(token);
            } else {
                throw new Exception("Invalid character");
            }
        }

        // Pop all remaining operators from the stack to postfix expression
        while (!operatorStack.isEmpty()) {
            char top = operatorStack.pop();
            if (top == '(' || top == ')') {
                throw new Exception("Mismatched parentheses");
            }
            postfix.append(' ').append(top);
        }

        return postfix.toString();
    }

    // Method to evaluate a postfix expression
    private static double evaluatePostfix(String postfix) throws Exception {
        Stack<Double> valueStack = new Stack<>();
        Scanner scanner = new Scanner(postfix);

        while (scanner.hasNext()) {
            if (scanner.hasNextDouble()) {
                // Push number to stack
                valueStack.push(scanner.nextDouble());
            } else {
                // Get the next token
                String token = scanner.next();
                if (token.length() == 1 && isOperator(token.charAt(0))) {
                    char operator = token.charAt(0);
                    if (valueStack.size() < 2) {
                        throw new Exception("Invalid postfix expression");
                    }
                    // Pop two values from stack
                    double b = valueStack.pop();
                    double a = valueStack.pop();
                    // Perform the operation based on the operator
                    switch (operator) {
                        case '+':
                            valueStack.push(a + b);
                            break;
                        case '-':
                            valueStack.push(a - b);
                            break;
                        case '*':
                            valueStack.push(a * b);
                            break;
                        case '/':
                            if (b == 0) {
                                throw new Exception("Division by zero");
                            }
                            valueStack.push(a / b);
                            break;
                        default:
                            throw new Exception("Invalid operator");
                    }
                } else {
                    throw new Exception("Invalid token in postfix expression");
                }
            }
        }

        // There should be exactly one value left in the stack if the expression is
        // valid
        if (valueStack.size() != 1) {
            throw new Exception("Invalid postfix expression");
        }

        return valueStack.pop();
    }

    // Method to check if a character is an operator
    private static boolean isOperator(char token) {
        return token == '+' || token == '-' || token == '*' || token == '/';
    }

    // Method to get the precedence of operators
    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }
}
