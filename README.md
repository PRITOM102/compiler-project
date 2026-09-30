# Sylheti Compiler - Complete Terminal Output

This version shows the complete compiler pipeline in the terminal:

1. Source Code
2. Tokens / Lexical Analysis
3. Parse Tree
4. Semantic Analysis + Symbol Table
5. Program Output / Interpretation

## Folder

`new bangla compiler/banglacompiler/`

## Compile

Open PowerShell/Command Prompt in the `banglacompiler` folder and run:

```powershell
javac *.java
```

## Run

```powershell
java banglacompiler.Main
```

If your terminal is already inside the parent folder (`new bangla compiler`), use:

```powershell
javac banglacompiler\*.java
java banglacompiler.Main
```

## What was added

- `ParseTree.java` — builds and prints a tree using `├──` and `└──`.
- `Main.java` — prints Token Table, Parse Tree, Semantic Analysis, Symbol Table and Output.
- `SemanticAnalyzer.java` — prints the symbol table after successful semantic analysis.
- `Grammar.java` — corrected the displayed keywords to match the implemented language (`লও`, `দেহাও`).

## Important

The current parser implementation supports:
- variable declaration: `লও পুরা ক = ১০;`
- decimal declaration: `লও দশমিক ঘ = ১.৫;`
- assignment: `ক = ক + ১০০;`
- print: `দেহাও (ক);`
- arithmetic: `+ - * /`
- parentheses and unary minus

The displayed parse tree follows the grammar actually implemented by `Parser.java`.


## Windows PowerShell (recommended)

From the `new bangla compiler` folder:

```powershell
javac -encoding UTF-8 banglacompiler\*.java
java -Dfile.encoding=UTF-8 banglacompiler.Main
```

Or, from the ZIP's top-level folder, double-click `run.bat`.

## Terminal sections

When the program runs, you will see:

```text
[1] SOURCE CODE

[2] TOKENS (LEXICAL ANALYSIS)
Line  Token Type             Lexeme             Literal
...

[3] PARSE TREE
└── Program
    ├── Declaration
    │   ├── KW_LAO : লও
    │   ├── KW_PURA : পুরা
    │   └── ...
    └── PrintStatement
        └── ...

[4] SEMANTIC ANALYSIS
[OK] No semantic errors found.

Symbol Table:
Variable             Type
...

[5] OUTPUT
60
20
200
...
```

### If you only want to see the stages

You do not need to run separate commands for Token / Parse Tree / Semantic Analysis. A single:

```powershell
java -Dfile.encoding=UTF-8 banglacompiler.Main
```

prints all stages in order.
