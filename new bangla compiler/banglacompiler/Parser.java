package banglacompiler;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Lexer.Token> tokens;
    public final List<String> errors = new ArrayList<>();
    private int current = 0;

    public Parser(List<Lexer.Token> tokens) {
        this.tokens = tokens;
    }

    public void parseProgram() {
        while (!isAtEnd()) {
            try {
                parseStatement();
            } catch (RuntimeException e) {
                errors.add("Line " + peek().line + ": " + e.getMessage());
                synchronize();
            }
        }
    }

    private void parseStatement() {
        if (match(Lexer.TokenType.KW_LAO)) {
            parseDeclaration();
            return;
        }
        if (check(Lexer.TokenType.IDENTIFIER)) {
            parseAssignment();
            return;
        }
        if (match(Lexer.TokenType.KW_DEHAO)) {
            parsePrint();
            return;
        }
        throw error("Expected statement");
    }

    private void parseDeclaration() {
        if (!match(Lexer.TokenType.KW_PURA, Lexer.TokenType.KW_DOSHOMIK)) {
            throw error("Expected পুরা or দশমিক");
        }
        consume(Lexer.TokenType.IDENTIFIER, "Expected variable name");
        consume(Lexer.TokenType.ASSIGN, "Expected =");
        parseExpression();
        consume(Lexer.TokenType.SEMI, "Expected ;");
    }

    private void parseAssignment() {
        consume(Lexer.TokenType.IDENTIFIER, "Expected variable");
        consume(Lexer.TokenType.ASSIGN, "Expected =");
        parseExpression();
        consume(Lexer.TokenType.SEMI, "Expected ;");
    }

    private void parsePrint() {
        consume(Lexer.TokenType.LPAREN, "Expected (");
        parseExpression();
        consume(Lexer.TokenType.RPAREN, "Expected )");
        consume(Lexer.TokenType.SEMI, "Expected ;");
    }

    private void parseExpression() {
        parseTerm();
        while (match(Lexer.TokenType.PLUS, Lexer.TokenType.MINUS)) {
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();
        while (match(Lexer.TokenType.STAR, Lexer.TokenType.SLASH)) {
            parseFactor();
        }
    }

    private void parseFactor() {
        if (match(Lexer.TokenType.MINUS)) {
            parseFactor();
            return;
        }
        if (match(Lexer.TokenType.INT_LITERAL) || match(Lexer.TokenType.FLOAT_LITERAL) || match(Lexer.TokenType.IDENTIFIER)) {
            return;
        }
        if (match(Lexer.TokenType.LPAREN)) {
            parseExpression();
            consume(Lexer.TokenType.RPAREN, "Expected )");
            return;
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

    private void synchronize() {
        if (!isAtEnd()) {
            advance();
        }
        while (!isAtEnd()) {
            if (previous().type == Lexer.TokenType.SEMI) {
                return;
            }
            if (check(Lexer.TokenType.KW_LAO) || check(Lexer.TokenType.KW_DEHAO) || check(Lexer.TokenType.IDENTIFIER)) {
                return;
            }
            advance();
        }
    }
}
