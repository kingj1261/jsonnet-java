package json.execute.entity;

public enum Kind {

    // Symbols
    BRACE_L,
    BRACE_R,
    BRACKET_L,
    BRACKET_R,
    COLON,
    COMMA,
    DOLLAR,
    DOT,
    PAREN_L,
    PAREN_R,
    SEMICOLON,

    // Arbitrary length lexemes
    IDENTIFIER,
    NUMBER,
    OPERATOR,
    STRING,

    // Keywords
    ELSE,
    ERROR,
    FALSE,
    FOR,
    FUNCTION,
    IF,
    IMPORT,
    IMPORTSTR,
    IN,
    LOCAL,
    NULL_LIT,
    TAILCALL,
    THEN,
    SELF,
    SUPER,
    TRUE,

    // A special token that holds line/column information about the end of the file.
    END_OF_FILE

}
