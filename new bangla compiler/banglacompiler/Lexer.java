package banglacompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    public enum TokenType {
        KW_LAO, KW_PURA, KW_DOSHOMIK, KW_JODI, KW_NAILE, KW_JOTOKKHON, KW_DEHAO,
        IDENTIFIER, INT_LITERAL, FLOAT_LITERAL,
        ASSIGN, EQ, NEQ, LT, GT, LE, GE,
        PLUS, MINUS, STAR, SLASH,
        LPAREN, RPAREN, LBRACE, RBRACE, SEMI, EOF
    }

    public static class Token {

        public final TokenType type;
        public final String lexeme;
        public final Object literal;
        public final int line;

        public Token(TokenType type, String lexeme, Object literal, int line) {
            this.type = type;
            this.lexeme = lexeme;
            this.literal = literal;
            this.line = line;
        }

        @Override
        public String toString() {
            return type + " " + lexeme;
        }
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    public final List<String> errors = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("লও", TokenType.KW_LAO);
        KEYWORDS.put("পুরা", TokenType.KW_PURA);
        KEYWORDS.put("দশমিক", TokenType.KW_DOSHOMIK);
        KEYWORDS.put("যদি", TokenType.KW_JODI);
        KEYWORDS.put("নাইলে", TokenType.KW_NAILE);
        KEYWORDS.put("যতক্ষণ", TokenType.KW_JOTOKKHON);
        KEYWORDS.put("দেহাও", TokenType.KW_DEHAO);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '#':
                while (peek() != '\n' && !isAtEnd()) {
                    advance();
                }
                break;
            case '(':
                addToken(TokenType.LPAREN);
                break;
            case ')':
                addToken(TokenType.RPAREN);
                break;
            case '{':
                addToken(TokenType.LBRACE);
                break;
            case '}':
                addToken(TokenType.RBRACE);
                break;
            case ';':
                addToken(TokenType.SEMI);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '/':
                if (peek() == '/') {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case '=':
                addToken(match('=') ? TokenType.EQ : TokenType.ASSIGN);
                break;
            case '!':
                if (match('=')) {
                    addToken(TokenType.NEQ);
                } else {
                    errors.add("Line " + line + ": unexpected character !");
                }
                break;
            case '<':
                addToken(match('=') ? TokenType.LE : TokenType.LT);
                break;
            case '>':
                addToken(match('=') ? TokenType.GE : TokenType.GT);
                break;
            default:
                if (Character.isDigit(c)) {
                    number();
                } else if (Character.isLetter(c) || c == '_' || isBangla(c)) {
                    identifier();
                } else {
                    errors.add("Line " + line + ": unexpected character '" + c + "'");
                }
                break;
        }
    }

    private boolean isBangla(char c) {
        return c >= '\u0980' && c <= '\u09FF';
    }

    private boolean isIdentifierPart(char c) {
        if (Character.isLetterOrDigit(c) || c == '_' || isBangla(c)) {
            return true;
        }
        int type = Character.getType(c);
        return type == Character.NON_SPACING_MARK
                || type == Character.COMBINING_SPACING_MARK
                || type == Character.ENCLOSING_MARK;
    }

    private void identifier() {
        while (isIdentifierPart(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    private void number() {
        boolean decimal = false;
        while (Character.isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && Character.isDigit(peekNext())) {
            decimal = true;
            advance();
            while (Character.isDigit(peek())) {
                advance();
            }
        }

        String text = source.substring(start, current);
        String engText = banglaToEnglishNumber(text);

        if (decimal) {
            tokens.add(new Token(TokenType.FLOAT_LITERAL, text, Double.parseDouble(engText), line));
        } else {
            tokens.add(new Token(TokenType.INT_LITERAL, text, Integer.parseInt(engText), line));
        }
    }

    private String banglaToEnglishNumber(String banglaNum) {
        StringBuilder builder = new StringBuilder();
        for (char c : banglaNum.toCharArray()) {
            if (c >= '\u09E6' && c <= '\u09EF') {
                builder.append((char) (c - '\u09E6' + '0'));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type, source.substring(start, current), null, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
