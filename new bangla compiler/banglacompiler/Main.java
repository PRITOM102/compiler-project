package banglacompiler;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String sourceCode
                = "# Sylheti Compiler example\n"
                + "লও পুরা ক = ৩০;\n"
                + "লও পুরা খ = ২০;\n"
                + "লও পুরা গ = ৫০;\n"
                + "লও দশমিক ঘ = ২.৫;\n"
                + "লও দশমিক ঙ = ৩.৫;\n"
                + "লও পুরা যোগফল = ক + খ + গ;\n"
                + "দেহাও (যোগফল);\n"
                + "লও পুরা বিয়োগফল = গ - ক;\n"
                + "দেহাও (বিয়োগফল);\n"
                + "লও পুরা গুণফল = ক * খ;\n"
                + "দেহাও (গুণফল);\n"
                + "লও দশমিক ভাগফল = ঙ / ঘ;\n"
                + "দেহাও (ভাগফল);\n"
                + "লও দশমিক হিসাব = (ক + খ) * ঘ - ঙ / ২;\n"
                + "দেহাও (হিসাব);\n"
                + "ক = ক + ১০০;\n"
                + "খ = খ * ২;\n"
                + "গ = গ - ৩;\n"
                + "দেহাও (ক);\n"
                + "দেহাও (খ);\n"
                + "দেহাও (গ);\n"
                + "যোগফল = ক + খ + গ;\n"
                + "দেহাও (যোগফল);\n";

        System.out.println("==================================================");
        System.out.println("              SYLHETI COMPILER");
        System.out.println("==================================================");

        System.out.println("\n[1] SOURCE CODE");
        System.out.println("--------------------------------------------------");
        System.out.println(sourceCode);

        // ---------------- LEXICAL ANALYSIS ----------------
        Lexer lexer = new Lexer(sourceCode);
        List<Lexer.Token> tokens = lexer.scanTokens();

        System.out.println("[2] TOKENS (LEXICAL ANALYSIS)");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-5s %-22s %-18s %-8s%n",
                "Line", "Token Type", "Lexeme", "Literal");
        System.out.println("--------------------------------------------------");

        for (Lexer.Token token : tokens) {
            String lexeme = token.lexeme.replace("\n", "\\n");
            String literal = token.literal == null ? "-" : token.literal.toString();
            System.out.printf("%-5d %-22s %-18s %-8s%n",
                    token.line, token.type, lexeme, literal);
        }

        if (!lexer.errors.isEmpty()) {
            System.out.println("\n[LEXICAL ERRORS]");
            for (String err : lexer.errors) System.out.println("  " + err);
            return;
        }
        System.out.println("\n[OK] Lexical Analysis completed.");

        // ---------------- PARSING ----------------
        Parser parser = new Parser(tokens);
        parser.parseProgram();

        System.out.println("\n[3] PARSE TREE");
        System.out.println("--------------------------------------------------");
        if (parser.errors.isEmpty()) {
            try {
                new ParseTree(tokens).print();
                System.out.println("\n[OK] Parsing completed.");
            } catch (RuntimeException e) {
                System.out.println("[ERROR] Could not build parse tree: " + e.getMessage());
                return;
            }
        } else {
            for (String err : parser.errors) System.out.println("  " + err);
            return;
        }

        // ---------------- SEMANTIC ANALYSIS ----------------
        SemanticAnalyzer analyzer = new SemanticAnalyzer(tokens);
        analyzer.analyze();

        System.out.println("\n[4] SEMANTIC ANALYSIS");
        System.out.println("--------------------------------------------------");
        if (analyzer.errors.isEmpty()) {
            System.out.println("[OK] No semantic errors found.");
            System.out.println("\nSymbol Table:");
            analyzer.printSymbolTable();
        } else {
            for (String err : analyzer.errors) System.out.println("  " + err);
            return;
        }

        // ---------------- INTERPRETATION ----------------
        System.out.println("\n[5] OUTPUT");
        System.out.println("--------------------------------------------------");
        try {
            Interpreter interpreter = new Interpreter(tokens);
            interpreter.run();
            System.out.println("--------------------------------------------------");
            System.out.println("[OK] Program execution completed.");
        } catch (RuntimeException e) {
            System.out.println("[RUNTIME ERROR] " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("              COMPILATION FINISHED");
        System.out.println("==================================================");
    }
}
