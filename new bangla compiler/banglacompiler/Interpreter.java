package banglacompiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

    private final List<Lexer.Token> tokens;
    private int current = 0;
    private final Map<String, Double> variables = new HashMap<>();

    public Interpreter(List<Lexer.Token> tokens) {
        this.tokens = tokens;
    }

    public void run() {
        current = 0;
        variables.clear();
        while (!isAtEnd()) {
            executeStatement();
        }
    }

    private void executeStatement() {
        if (match(Lexer.TokenType.KW_LAO)) {
            executeDeclaration();
            return;
        }
        if (check(Lexer.TokenType.IDENTIFIER)) {
            executeAssignment();
            return;
        }
        if (match(Lexer.TokenType.KW_DEHAO)) {
            executePrint();
            return;
        }
        throw error("Invalid statement");
    }

    private void executeDeclaration() {
        if (!match(Lexer.TokenType.KW_PURA, Lexer.TokenType.KW_DOSHOMIK)) {
            throw error("Expected type");
        }
        Lexer.Token name = consume(Lexer.TokenType.IDENTIFIER, "Expected variable name");
        consume(Lexer.TokenType.ASSIGN, "Expected =");
        double value = expression();
        variables.put(name.lexeme, value);
        consume(Lexer.TokenType.SEMI, "Expected ;");
    }

    private void executeAssignment() {
        Lexer.Token name = consume(Lexer.TokenType.IDENTIFIER, "Expected variable");
        consume(Lexer.TokenType.ASSIGN, "Expected =");
        double value = expression();
        if (!variables.containsKey(name.lexeme)) {
            throw error("Variable '" + name.lexeme + "' not declared");
        }
        variables.put(name.lexeme, value);
        consume(Lexer.TokenType.SEMI, "Expected ;");
    }

    private void executePrint() {
        consume(Lexer.TokenType.LPAREN, "Expected (");
        double value = expression();
        consume(Lexer.TokenType.RPAREN, "Expected )");
        consume(Lexer.TokenType.SEMI, "Expected ;");
        if (value == (long) value) {
            System.out.println((long) value);
        } else {
            System.out.println(value);
        }
    }

    private double expression() {
        double value = term();
        while (match(Lexer.TokenType.PLUS, Lexer.TokenType.MINUS)) {
            Lexer.TokenType op = previous().type;
            double right = term();
            if (op == Lexer.TokenType.PLUS) {
                value += right;
            } else {
                value -= right;
            }
        }
        return value;
    }

    private double term() {
        double value = factor();
        while (match(Lexer.TokenType.STAR, Lexer.TokenType.SLASH)) {
            Lexer.TokenType op = previous().type;
            double right = factor();
            if (op == Lexer.TokenType.STAR) {
                value *= right;
            } else {
                if (right == 0) {
                    throw error("Division by zero");
                }
                value /= right;
            }
        }
        return value;
    }

    private double factor() {
        if (match(Lexer.TokenType.MINUS)) {
            return -factor();
        }
        if (match(Lexer.TokenType.INT_LITERAL)) {
            return ((Integer) previous().literal).doubleValue();
        }
        if (match(Lexer.TokenType.FLOAT_LITERAL)) {
            return (Double) previous().literal;
        }
        if (match(Lexer.TokenType.IDENTIFIER)) {
            String name = previous().lexeme;
            if (!variables.containsKey(name)) {
                throw error("Variable '" + name + "' not declared");
            }
            return variables.get(name);
        }
        if (match(Lexer.TokenType.LPAREN)) {
            double value = expression();
            consume(Lexer.TokenType.RPAREN, "Expected )");
            return value;
        }
        throw error("Expected expression");
    }

    private boolean match(Lexer.TokenType... types) {
        for (Lexer.TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Lexer.Token consume(Lexer.TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(message);
    }

    private boolean check(Lexer.TokenType type) {
        if (isAtEnd()) {
            return type == Lexer.TokenType.EOF;
        }
        return peek().type == type;
    }

    private Lexer.Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Lexer.Token peek() {
        return tokens.get(current);
    }

    private Lexer.Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type == Lexer.TokenType.EOF;
    }

    private RuntimeException error(String message) {
        return new RuntimeException(message);
    }
}
