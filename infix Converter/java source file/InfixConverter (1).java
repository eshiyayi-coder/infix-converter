import java.util.*;

public class InfixConverter {
    
    // Map to store operator precedence
    private static final Map<Character, Integer> precedence = new HashMap<>();
    
    static {
        precedence.put('+', 1);
        precedence.put('-', 1);
        precedence.put('*', 2);
        precedence.put('/', 2);
        precedence.put('^', 3);
    }
    
    /**
     * Check if character is an operator
     */
    private static boolean isOperator(char c) {
        return precedence.containsKey(c);
    }
    
    /**
     * Check if character is an operand (letter or digit)
     */
    private static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }
    
    /**
     * Get precedence of an operator
     */
    private static int getPrecedence(char op) {
        return precedence.getOrDefault(op, -1);
    }
    
    /**
     * Check if operator is right associative
     */
    private static boolean isRightAssociative(char op) {
        return op == '^';
    }
    
    /**
     * Convert infix expression to postfix (Shunting Yard Algorithm)
     * Example: "a+b*c" → "abc*+"
     */
    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        
        for (char c : infix.toCharArray()) {
            // If operand, add to output
            if (isOperand(c)) {
                postfix.append(c);
            }
            // If '(', push to stack
            else if (c == '(') {
                stack.push(c);
            }
            // If ')', pop until matching '('
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop(); // Remove '('
                }
            }
            // If operator
            else if (isOperator(c)) {
                while (!stack.isEmpty() && 
                       stack.peek() != '(' &&
                       isOperator(stack.peek()) &&
                       (getPrecedence(stack.peek()) > getPrecedence(c) ||
                        (getPrecedence(stack.peek()) == getPrecedence(c) && 
                         !isRightAssociative(c)))) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }
        }
        
        // Pop remaining operators
        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        
        return postfix.toString();
    }
    
    /**
     * Convert infix expression to prefix
     * Method: Reverse → Modify → Convert to Postfix → Reverse
     * Example: "a+b*c" → "+a*bc"
     */
    public static String infixToPrefix(String infix) {
        // Step 1: Reverse the infix expression
        StringBuilder reversed = new StringBuilder(infix).reverse();
        
        // Step 2: Swap parentheses
        StringBuilder modifiedInfix = new StringBuilder();
        for (char c : reversed.toString().toCharArray()) {
            if (c == '(') {
                modifiedInfix.append(')');
            } else if (c == ')') {
                modifiedInfix.append('(');
            } else {
                modifiedInfix.append(c);
            }
        }
        
        // Step 3: Convert to postfix (with modified expression)
        String postfix = infixToPostfix(modifiedInfix.toString());
        
        // Step 4: Reverse the postfix to get prefix
        return new StringBuilder(postfix).reverse().toString();
    }
    
    /**
     * Evaluate postfix expression
     */
    public static double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        
        for (char c : postfix.toCharArray()) {
            if (isOperand(c)) {
                // Convert character to its numeric value (for single digits)
                stack.push((double) (c - '0'));
            } else if (isOperator(c)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                double b = stack.pop();
                double a = stack.pop();
                double result = performOperation(a, b, c);
                stack.push(result);
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression");
        }
        
        return stack.pop();
    }
    
    /**
     * Perform arithmetic operation
     */
    private static double performOperation(double a, double b, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': 
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case '^': return Math.pow(a, b);
            default: throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
    
    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        // Test cases
        String[] testExpressions = {
            "a+b*c",
            "(a+b)*c",
            "a*(b+c)",
            "a+b*c-d/e",
            "((a+b)*c)-d",
            "a^b*c+d",
            "(a+b)*(c+d)"
        };
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     INFIX TO POSTFIX AND PREFIX CONVERTER                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        for (String expr : testExpressions) {
            String postfix = infixToPostfix(expr);
            String prefix = infixToPrefix(expr);
            
            System.out.println("Infix:   " + expr);
            System.out.println("Postfix: " + postfix);
            System.out.println("Prefix:  " + prefix);
            System.out.println();
        }
        
        // Example with numeric evaluation
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              POSTFIX EXPRESSION EVALUATION                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        String numericExpr = "5+3*2";  // Should be 11 (not 16)
        String postfix = infixToPostfix(numericExpr);
        double result = evaluatePostfix(postfix);
        
        System.out.println("Infix:    " + numericExpr);
        System.out.println("Postfix:  " + postfix);
        System.out.println("Result:   " + result);
    }
}