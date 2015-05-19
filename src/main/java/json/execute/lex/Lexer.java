package json.execute.lex;

import json.execute.entity.Kind;
import json.execute.entity.State;
import json.execute.entity.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public List<Token> lexJsonnet(String jsonnet) {
        List<Token> tokens = new ArrayList<Token>();
        long lineNumber = 1;

        for (int i = 0; i < jsonnet.length(); i++) {
            Kind kind = null;
            String data = "";

            switch (jsonnet.charAt(i)) {
                // Skip non-\n whitespace
                case ' ':
                case '\t':
                case '\r':
                    continue;
                    // Skip \n and maintain line numbers
                case '\n':
                    lineNumber++;
                    continue;
                case '{':
                    kind = Kind.BRACE_L;
                    break;
                case '}':
                    kind = Kind.BRACE_R;
                    break;
                case ':':
                    kind = Kind.COLON;
                    break;
                case '(':
                    kind = Kind.PAREN_L;
                    break;
                case ')':
                    kind = Kind.PAREN_R;
                    break;

                case '+':
                    kind = Kind.OPERATOR;
                    data = "+";
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
                    data = lexNumber(jsonnet, i);
                    break;

                // String literals.
                case '"': {
                    i++;
                    for (; ; ++i) {
                        if (jsonnet.charAt(i) == '\0') {
                            throw new RuntimeException("Unterminated string");
                        }
                        if (jsonnet.charAt(i) == '"') {
                            break;
                        }
                        switch (jsonnet.charAt(i)) {
                            default:
                                // Just a regular letter.
                                data += jsonnet.charAt(i);
                        }
                    }
                    kind = Kind.STRING;
                }
                break;
            }
            Token token = new Token(kind, data);
            tokens.add(token);
        }
        Token token = new Token(Kind.END_OF_FILE, "");
        tokens.add(token);
        return tokens;
    }

    private String lexNumber(String jsonnet, int i) {
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
                            return r;
                    }
                    break;
            }
            r += jsonnet.charAt(i);
            i++;
        }
    }
}