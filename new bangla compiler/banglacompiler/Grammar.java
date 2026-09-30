package banglacompiler;

public class Grammar {

    public static void showGrammar() {
        System.out.println("Sylheti Language Grammar");
        System.out.println();

        System.out.println("program     -> statement* EOF");

        System.out.println("statement   -> varDecl");
        System.out.println("             | assignment");
        System.out.println("             | printStmt");
        System.out.println("             | ifStmt");
        System.out.println("             | whileStmt");

        System.out.println();

        System.out.println("varDecl     -> 'লও' type IDENTIFIER '=' expression ';'");

        System.out.println("type        -> 'পূর্ণ'");
        System.out.println("             | 'দশমিক'");

        System.out.println();

        System.out.println("assignment  -> IDENTIFIER '=' expression ';'");

        System.out.println();

        System.out.println("printStmt   -> 'দেহাও' '(' expression ')' ';'");

        System.out.println();

        System.out.println("ifStmt      -> 'যদি' '(' condition ')' block");
        System.out.println("               ('নাহলে' block)?");

        System.out.println();

        System.out.println("whileStmt   -> 'যতক্ষণ' '(' condition ')' block");

        System.out.println();

        System.out.println("block       -> '{' statement* '}'");

        System.out.println();

        System.out.println("condition   -> expression comparison expression");

        System.out.println("comparison  -> '=='");
        System.out.println("             | '!='");
        System.out.println("             | '<'");
        System.out.println("             | '>'");
        System.out.println("             | '<='");
        System.out.println("             | '>='");

        System.out.println();

        System.out.println("expression  -> term (('+' | '-') term)*");

        System.out.println("term        -> factor (('*' | '/') factor)*");

        System.out.println();

        System.out.println("factor      -> INTEGER");
        System.out.println("             | FLOAT");
        System.out.println("             | IDENTIFIER");
        System.out.println("             | '-' factor");
        System.out.println("             | '(' expression ')'");
    }
}