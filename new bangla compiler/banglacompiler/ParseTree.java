package banglacompiler;

import java.util.List;

/**
 * Builds and prints a readable parse tree from the token stream.
 * The tree follows the grammar implemented by Parser.java.
 */
public class ParseTree {

    private final List<Lexer.Token> tokens;
    private int current = 0;

    public ParseTree(List<Lexer.Token> tokens) {
        this.tokens = tokens;
    }

    public void print() {
        Node root = parseProgram();
        root.print("", true);
    }

    private Node parseProgram() {
        Node node = new Node("Program");
        while (!isAtEnd()) {
            node.add(parseStatement());
        }
        node.add(new Node("EOF"));
        return node;
    }

    private Node parseStatement() {
        if (match(Lexer.TokenType.KW_LAO)) {
            return parseDeclaration(previous());
        }
        if (check(Lexer.TokenType.IDENTIFIER)) {
            return parseAssignment();
        }
        if (match(Lexer.TokenType.KW_DEHAO)) {
            return parsePrint(previous());
        }
        throw error("Expected statement");
    }

    private Node parseDeclaration(Lexer.Token keyword) {
        Node node = new Node("Declaration");
        node.add(tokenNode(keyword));

        Lexer.Token type = consumeAny(
                Lexer.TokenType.KW_PURA,
                Lexer.TokenType.KW_DOSHOMIK,
                "Expected type"
        );
        node.add(tokenNode(type));

        node.add(tokenNode(consume(Lexer.TokenType.IDENTIFIER, "Expected variable name")));
        node.add(tokenNode(consume(Lexer.TokenType.ASSIGN, "Expected =")));
        node.add(parseExpression());
        node.add(tokenNode(consume(Lexer.TokenType.SEMI, "Expected ;")));
        return node;
    }

    private Node parseAssignment() {
        Node node = new Node("Assignment");
        node.add(tokenNode(consume(Lexer.TokenType.IDENTIFIER, "Expected variable")));
        node.add(tokenNode(consume(Lexer.TokenType.ASSIGN, "Expected =")));
        node.add(parseExpression());
        node.add(tokenNode(consume(Lexer.TokenType.SEMI, "Expected ;")));
        return node;
    }

    private Node parsePrint(Lexer.Token keyword) {
        Node node = new Node("PrintStatement");
        node.add(tokenNode(keyword));
        node.add(tokenNode(consume(Lexer.TokenType.LPAREN, "Expected (")));
        node.add(parseExpression());
        node.add(tokenNode(consume(Lexer.TokenType.RPAREN, "Expected )")));
        node.add(tokenNode(consume(Lexer.TokenType.SEMI, "Expected ;")));
        return node;
    }

    private Node parseExpression() {
        Node left = parseTerm();
        while (match(Lexer.TokenType.PLUS, Lexer.TokenType.MINUS)) {
            Lexer.Token op = previous();
            Node right = parseTerm();
            Node parent = new Node("Expression");
            parent.add(left);
            parent.add(tokenNode(op));
            parent.add(right);
            left = parent;
        }
        return left;
    }

    private Node parseTerm() {
        Node left = parseFactor();
        while (match(Lexer.TokenType.STAR, Lexer.TokenType.SLASH)) {
            Lexer.Token op = previous();
            Node right = parseFactor();
            Node parent = new Node("Term");
            parent.add(left);
            parent.add(tokenNode(op));
            parent.add(right);
            left = parent;
        }
        return left;
    }

    private Node parseFactor() {
        if (match(Lexer.TokenType.MINUS)) {
            Node node = new Node("UnaryFactor");
            node.add(tokenNode(previous()));
            node.add(parseFactor());
            return node;
        }

        if (match(Lexer.TokenType.INT_LITERAL, Lexer.TokenType.FLOAT_LITERAL,
                Lexer.TokenType.IDENTIFIER)) {
            return tokenNode(previous());
        }

        if (match(Lexer.TokenType.LPAREN)) {
            Node node = new Node("GroupedExpression");
            node.add(tokenNode(previous()));
            node.add(parseExpression());
            node.add(tokenNode(consume(Lexer.TokenType.RPAREN, "Expected )")));
            return node;
        }

        throw error("Expected expression");
    }

    private Node tokenNode(Lexer.Token token) {
        if (token == null) return new Node("<missing token>");
        String text = token.lexeme == null || token.lexeme.isEmpty()
                ? token.type.toString()
                : token.type + " : " + token.lexeme;
        return new Node(text + " [line " + token.line + "]");
    }

    private Lexer.Token consume(Lexer.TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message);
    }

    private Lexer.Token consumeAny(Lexer.TokenType a, Lexer.TokenType b, String message) {
        if (check(a) || check(b)) return advance();
        throw error(message);
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

    private boolean check(Lexer.TokenType type) {
        if (isAtEnd()) return type == Lexer.TokenType.EOF;
        return peek().type == type;
    }

    private Lexer.Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Lexer.Token peek() {
        return tokens.get(current);
    }

    private Lexer.Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return current >= tokens.size() || peek().type == Lexer.TokenType.EOF;
    }

    private RuntimeException error(String message) {
        return new RuntimeException(message + " near token " +
                (isAtEnd() ? "EOF" : peek().lexeme));
    }

    private static class Node {
        private final String label;
        private final java.util.List<Node> children = new java.util.ArrayList<>();

        Node(String label) {
            this.label = label;
        }

        void add(Node child) {
            if (child != null) children.add(child);
        }

        void print(String prefix, boolean last) {
            System.out.println(prefix + (last ? "└── " : "├── ") + label);
            String childPrefix = prefix + (last ? "    " : "│   ");
            for (int i = 0; i < children.size(); i++) {
                children.get(i).print(childPrefix, i == children.size() - 1);
            }
        }
    }
}
