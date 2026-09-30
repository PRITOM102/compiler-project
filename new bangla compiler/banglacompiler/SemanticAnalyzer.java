package banglacompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {

    private final List<Lexer.Token> tokens;
    public final List<String> errors = new ArrayList<>();

    private int current = 0;

    private final Map<String, Lexer.TokenType> symbolTable = new HashMap<>();

    public SemanticAnalyzer(List<Lexer.Token> tokens) {
        this.tokens = tokens;
    }

    public void analyze() {
        while (!isAtEnd()) {
            analyzeStatement();
        }
    }

    private void analyzeStatement() {
        if (match(Lexer.TokenType.KW_LAO)) {
            analyzeDeclaration();
        } else if (match(Lexer.TokenType.IDENTIFIER)) {
            analyzeAssignment();
        } else if (match(Lexer.TokenType.KW_DEHAO)) {
            analyzePrint();
        } else {
            advance();
        }
    }

    private void analyzeDeclaration() {
        Lexer.TokenType varType = null;

        if (match(Lexer.TokenType.KW_PURA)) {
            varType = Lexer.TokenType.KW_PURA;
        } else if (match(Lexer.TokenType.KW_DOSHOMIK)) {
            varType = Lexer.TokenType.KW_DOSHOMIK;
        }

        Lexer.Token nameToken = consume(Lexer.TokenType.IDENTIFIER);

        if (nameToken != null) {

            if (symbolTable.containsKey(nameToken.lexeme)) {
                errors.add("Line " + nameToken.line + ": Semantic Error - Variable '" + nameToken.lexeme + "' is already declared.");
            } else {
                symbolTable.put(nameToken.lexeme, varType);
            }
        }

        consume(Lexer.TokenType.ASSIGN);

        Lexer.TokenType exprType = analyzeExpression();

        if (nameToken != null && varType == Lexer.TokenType.KW_PURA && exprType == Lexer.TokenType.FLOAT_LITERAL) {
            errors.add("Line " + nameToken.line + ": Semantic Warning - Assigning a decimal value to whole number variable '" + nameToken.lexeme + "'.");
        }

        consume(Lexer.TokenType.SEMI);
    }

    private void analyzeAssignment() {
        Lexer.Token nameToken = previous();

        if (!symbolTable.containsKey(nameToken.lexeme)) {
            errors.add("Line " + nameToken.line + ": Semantic Error - Variable '" + nameToken.lexeme + "' is used before it is declared.");
        }

        consume(Lexer.TokenType.ASSIGN);
        analyzeExpression();
        consume(Lexer.TokenType.SEMI);
    }

    private void analyzePrint() {
        consume(Lexer.TokenType.LPAREN);
        analyzeExpression();
        consume(Lexer.TokenType.RPAREN);
        consume(Lexer.TokenType.SEMI);
    }

    private Lexer.TokenType analyzeExpression() {
        Lexer.TokenType type = analyzeTerm();

        while (match(Lexer.TokenType.PLUS, Lexer.TokenType.MINUS)) {
            Lexer.TokenType rightType = analyzeTerm();

            if (rightType == Lexer.TokenType.FLOAT_LITERAL) {
                type = Lexer.TokenType.FLOAT_LITERAL;
            }
        }
        return type;
    }

    private Lexer.TokenType analyzeTerm() {
        Lexer.TokenType type = analyzeFactor();

        while (match(Lexer.TokenType.STAR, Lexer.TokenType.SLASH)) {
            Lexer.TokenType rightType = analyzeFactor();
            if (rightType == Lexer.TokenType.FLOAT_LITERAL || previous().type == Lexer.TokenType.SLASH) {
                type = Lexer.TokenType.FLOAT_LITERAL;
            }
        }
        return type;
    }

    private Lexer.TokenType analyzeFactor() {
        if (match(Lexer.TokenType.MINUS)) {
            return analyzeFactor();
        }
        if (match(Lexer.TokenType.INT_LITERAL)) {
            return Lexer.TokenType.INT_LITERAL;
        }
        if (match(Lexer.TokenType.FLOAT_LITERAL)) {
            return Lexer.TokenType.FLOAT_LITERAL;
        }
        if (match(Lexer.TokenType.IDENTIFIER)) {
            Lexer.Token nameToken = previous();

            if (!symbolTable.containsKey(nameToken.lexeme)) {
                errors.add("Line " + nameToken.line + ": Semantic Error - Variable '" + nameToken.lexeme + "' is not declared.");
                return Lexer.TokenType.INT_LITERAL;
            } else {
                Lexer.TokenType storedType = symbolTable.get(nameToken.lexeme);
                return storedType == Lexer.TokenType.KW_PURA ? Lexer.TokenType.INT_LITERAL : Lexer.TokenType.FLOAT_LITERAL;
            }
        }
        if (match(Lexer.TokenType.LPAREN)) {
            Lexer.TokenType type = analyzeExpression();
            consume(Lexer.TokenType.RPAREN);
            return type;
        }

        return Lexer.TokenType.INT_LITERAL;
    }

    public void printSymbolTable() {
        if (symbolTable.isEmpty()) {
            System.out.println("  (empty)");
            return;
        }
        System.out.printf("  %-20s %-15s%n", "Variable", "Type");
        System.out.println("  -----------------------------------");
        for (Map.Entry<String, Lexer.TokenType> entry : symbolTable.entrySet()) {
            String type = entry.getValue() == Lexer.TokenType.KW_PURA
                    ? "পুরা (INTEGER)"
                    : "দশমিক (FLOAT)";
            System.out.printf("  %-20s %-15s%n", entry.getKey(), type);
        }
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

    private Lexer.Token consume(Lexer.TokenType type) {
        if (check(type)) {
            return advance();
        }
        return null;
    }

    private boolean check(Lexer.TokenType type) {
        if (isAtEnd()) {
            return false;
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
        if (current >= tokens.size()) {
            return true;
        }
        return peek().type == Lexer.TokenType.EOF;
    }
}
