package json.execute.lex;

import json.execute.entity.*;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public static boolean is_upper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean is_lower(char c) {
        return c >= 'a' && c <= 'z';
    }

    public static boolean is_number(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean is_identifier_first(char c) {
        return is_upper(c) || is_lower(c) || c == '_';
    }

    public static boolean is_identifier(char c) {
        return is_identifier_first(c) || is_number(c);
    }

    public static boolean is_symbol(char c) {
        switch (c) {
            case '&':
            case '|':
            case '^':
            case '=':
            case '<':
            case '>':
            case '*':
            case '/':
            case '%':
            case '#':
                return true;
        }
        return false;
    }

    private String lex_number(String jsonnet, int i) {
        String r = "";
        State state = State.BEGIN;

        while (true) {
            switch (state) {
                case BEGIN:
                    switch (jsonnet.charAt(i)) {
                        case '0':
                            state = State.AFTER_ZERO;
                            break;
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_ONE_TO_NINE;
                            break;
                        default:
                            throw new RuntimeException("Couldn't lex number");
                    }
                    break;
                case AFTER_ZERO:
                    switch (jsonnet.charAt(i)) {
                        case '.':
                            state = State.AFTER_DOT;
                            break;
                        case 'e':
                        case 'E':
                            state = State.AFTER_E;
                            break;
                        default:
                            i--;
                            return r;
                    }
                    break;
                case AFTER_ONE_TO_NINE:
                    switch (jsonnet.charAt(i)) {
                        case '.':
                            state = State.AFTER_DOT;
                            break;

                        case 'e':
                        case 'E':
                            state = State.AFTER_E;
                            break;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_ONE_TO_NINE;
                            break;
                        default:
                            i--;
                            return r;
                    }
                    break;
                case AFTER_DOT:
                    switch (jsonnet.charAt(i)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_DIGIT;
                            break;

                        default: {
                            throw new RuntimeException("Couldn't lex number, junk after decimal point: " + jsonnet.charAt(i));
                        }
                    }
                    break;

                case AFTER_DIGIT:
                    switch (jsonnet.charAt(i)) {
                        case 'e':
                        case 'E':
                            state = State.AFTER_E;
                            break;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_DIGIT;
                            break;

                        default:
                            i--;
                            return r;
                    }
                    break;

                case AFTER_E:
                    switch (jsonnet.charAt(i)) {
                        case '+':
                        case '-':
                            state = State.AFTER_EXP_SIGN;
                            break;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_EXP_DIGIT;
                            break;

                        default: {
                            throw new RuntimeException("Couldn't lex number, junk after 'E': " + jsonnet.charAt(i));
                        }
                    }
                    break;

                case AFTER_EXP_SIGN:
                    switch (jsonnet.charAt(i)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_EXP_DIGIT;
                            break;

                        default: {
                            throw new RuntimeException("Couldn't lex number, junk after exponent sign: " + jsonnet.charAt(i));
                        }
                    }
                    break;

                case AFTER_EXP_DIGIT:
                    switch (jsonnet.charAt(i)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = State.AFTER_EXP_DIGIT;
                            break;

                        default:
                            i--;
                            return r;
                    }
                    break;
            }
            if (i < jsonnet.length()) {
                r += jsonnet.charAt(i);
                i++;
            }
        }
    }

    public List<Token> jsonnet_lex(String filename, String input) {
        long line_number = 1;
        long line_start = 0;

        List<Token> r = new ArrayList<Token>();

        for (int i = 0; i < input.length(); ++i) {
            Location begin = new Location(line_number, i - line_start + 1);
            Kind kind;
            String data = "";

            switch (input.charAt(i)) {

                // Skip non-\n whitespace
                case ' ':
                case '\t':
                case '\r':
                    continue;

                    // Skip \n and maintain line numbers
                case '\n':
                    line_number++;
                    line_start = i + 1;
                    continue;

                case '{':
                    kind = Kind.BRACE_L;
                    break;

                case '}':
                    kind = Kind.BRACE_R;
                    break;

                case '[':
                    kind = Kind.BRACKET_L;
                    break;

                case ']':
                    kind = Kind.BRACKET_R;
                    break;

                case ':':
                    kind = Kind.COLON;
                    break;

                case ',':
                    kind = Kind.COMMA;
                    break;

                case '$':
                    kind = Kind.DOLLAR;
                    break;

                case '.':
                    kind = Kind.DOT;
                    break;

                case '(':
                    kind = Kind.PAREN_L;
                    break;

                case ')':
                    kind = Kind.PAREN_R;
                    break;

                case ';':
                    kind = Kind.SEMICOLON;
                    break;

                // Special cases for unary operators.
                case '!':
                    kind = Kind.OPERATOR;
                    if (input.charAt(i + 1) == '=') {
                        i++;
                        data = "!=";
                    } else {
                        data = "!";
                    }
                    break;

                case '~':
                    kind = Kind.OPERATOR;
                    data = "~";
                    break;

                case '+':
                    kind = Kind.OPERATOR;
                    data = "+";

                    break;
                case '-':
                    kind = Kind.OPERATOR;
                    data = "-";
                    break;

                // Numeric literals.
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    kind = Kind.NUMBER;
                    data = lex_number(input, i);
                    break;

                // String literals.
                case '"': {
                    i++;
                    for (; ; ++i) {
                        if (input.charAt(i) == '\0') {
                            throw new RuntimeException("Unterminated string");
                        }
                        if (input.charAt(i) == '"') {
                            break;
                        }
                        switch (input.charAt(i)) {
                            case '\\':
                                switch (input.charAt(++i)) {
                                    case '"':
                                        data += input.charAt(i);
                                        break;

                                    case '\\':
                                        data += input.charAt(i);
                                        break;

                                    case '/':
                                        data += input.charAt(i);
                                        break;

                                    case 'b':
                                        data += '\b';
                                        break;

                                    case 'f':
                                        data += '\f';
                                        break;

                                    case 'n':
                                        data += '\n';
                                        break;

                                    case 'r':
                                        data += '\r';
                                        break;

                                    case 't':
                                        data += '\t';
                                        break;

                                    case 'u': {
                                        ++i;  // Consume the 'u'.
                                        long codepoint = 0;
                                        // Expect 4 hex digits.
                                        for (int j = 0; i < 4; ++i) {
                                            char x = input.charAt(j);
                                            int digit;
                                            if (x == '\0') {
                                                throw new RuntimeException("Unterminated string");
                                            } else if (x == '"') {
                                                throw new RuntimeException("Truncated unicode escape sequence in string literal.");
                                            } else if (x >= '0' && x <= '9') {
                                                digit = x - '0';
                                            } else if (x >= 'a' && x <= 'f') {
                                                digit = x - 'a' + 10;
                                            } else if (x >= 'A' && x <= 'F') {
                                                digit = x - 'A' + 10;
                                            } else {
                                                throw new RuntimeException("Malformed unicode escape character, should be hex: '" + x + "'");
                                            }
                                            codepoint *= 16;
                                            codepoint += digit;
                                        }

                                        // Encode in UTF-8.
                                        if (codepoint < 0x0080) {
                                            data += codepoint;
                                        } else {
                                            throw new RuntimeException("Codepoint out of ascii range.");
                                        }
/*
                                } else if (codepoint < 0x0800) {
                                    data += 0xC0 | (codepoint >> 6);
                                    data += 0x80 | (codepoint & 0x3F);
                                } else {
                                    data += 0xE0 | (codepoint >> 12);
                                    data += 0x80 | ((codepoint >> 6) & 0x3F);
                                    data += 0x80 | (codepoint & 0x3F);
                                }
*/
                                        // Leave us on the last char, ready for the ++c at
                                        // the outer for loop.
                                        i += 3;
                                    }
                                    break;

                                    case '\0': {
                                        throw new RuntimeException("Truncated escape sequence in string literal.");
                                    }

                                    default: {
                                        throw new RuntimeException("Unknown escape sequence in string literal: '" + input.charAt(i) + "'");
                                    }
                                }
                                break;

                            // Treat as a regular letter, but maintain line/column counters.
                            case '\n':
                                line_number++;
                                line_start = i + 1;
                                data += input.charAt(i);
                                break;

                            default:
                                // Just a regular letter.
                                data += input.charAt(i);
                        }
                    }
                    kind = Kind.STRING;
                }
                break;

                // Keywords
                default:
                    if (is_identifier_first(input.charAt(i))) {
                        String id = "";
                        for (; i != input.length(); ++i) {
                            if (!is_identifier(input.charAt(i))) {
                                break;
                            }
                            id += input.charAt(i);
                        }
                        --i;
                        if (id == "else") {
                            kind = Kind.ELSE;
                        } else if (id == "error") {
                            kind = Kind.ERROR;
                        } else if (id == "false") {
                            kind = Kind.FALSE;
                        } else if (id == "for") {
                            kind = Kind.FOR;
                        } else if (id == "function") {
                            kind = Kind.FUNCTION;
                        } else if (id == "if") {
                            kind = Kind.IF;
                        } else if (id == "import") {
                            kind = Kind.IMPORT;
                        } else if (id == "importstr") {
                            kind = Kind.IMPORTSTR;
                        } else if (id == "in") {
                            kind = Kind.IN;
                        } else if (id == "local") {
                            kind = Kind.LOCAL;
                        } else if (id == "null") {
                            kind = Kind.NULL_LIT;
                        } else if (id == "self") {
                            kind = Kind.SELF;
                        } else if (id == "super") {
                            kind = Kind.SUPER;
                        } else if (id == "tailcall") {
                            kind = Kind.TAILCALL;
                        } else if (id == "then") {
                            kind = Kind.THEN;
                        } else if (id == "true") {
                            kind = Kind.TRUE;
                        } else {
                            // Not a keyword, must be an identifier.
                            kind = Kind.IDENTIFIER;
                            data = id;
                        }
                    } else if (is_symbol(input.charAt(i))) {

                        // Single line C++ style comment
                        if (input.charAt(i) == '/' && input.charAt(i + 1) == '/') {
                            while (input.charAt(i) != '\0' && input.charAt(i) != '\n') {
                                ++i;
                            }
                            // Leaving it on the \n allows processing of \n on next iteration,
                            // i.e. managing of the line & column counter.
                            i--;
                            continue;
                        }

                        // Single line # comment
                        if (input.charAt(i) == '#') {
                            while (input.charAt(i) != '\0' && input.charAt(i) != '\n') {
                                ++i;
                            }
                            // Leaving it on the \n allows processing of \n on next iteration,
                            // i.e. managing of the line & column counter.
                            i--;
                            continue;
                        }

                        // Multi-line comment.
                        if (input.charAt(i) == '/' && input.charAt(i + 1) == '*') {
                            i += 2;  // Avoid matching /*/: skip the /* before starting the search for */.
                            while (input.charAt(i) != '\0' && !(input.charAt(i) == '*' && input.charAt(i + 1) == '/')) {
                                if (input.charAt(i) == '\n') {
                                    // Just keep track of the line / column counters.
                                    line_number++;
                                    line_start = i + 1;
                                }
                                ++i;
                            }
                            if (input.charAt(i) == '\0') {
                                throw new RuntimeException("Multi-line comment has no terminating */.");
                            }
                            // Leave the counter on the closing /.
                            i++;
                            continue;
                        }

                        for (; input.charAt(i) < input.length();
                             ++i) {
                            if (!is_symbol(input.charAt(i))) {
                                break;
                            }
                            data += input.charAt(i);
                        }
                        --i;
                        kind = Kind.OPERATOR;
                    } else {
                        throw new RuntimeException("Could not lex the character ");
                    }
                    break;
            }
            Location end = new Location(line_number, i - line_start + 1);
            r.add(new Token(kind, data, new LocationRange(filename, begin, end)));
        }
        Location end = new Location(line_number, (input.length() - 1) - line_start + 1);
        r.add(new Token(Kind.END_OF_FILE, "", new LocationRange(filename, end, end)));
        return r;
    }

}