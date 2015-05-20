package json.execute.parser;

import json.execute.entity.*;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.Identifier;
import json.execute.entity.ast.represents.*;
import json.execute.entity.ast.represents.binary.Binary;
import json.execute.entity.ast.represents.binary.BinaryOp;
import json.execute.entity.ast.represents.literal.LiteralString;
import json.execute.entity.ast.represents.object.Hide;
import json.execute.entity.ast.represents.object.ObjectComposition;
import json.execute.entity.ast.represents.object.ObjectConstructor;
import json.execute.entity.ast.represents.unary.Unary;
import json.execute.entity.ast.represents.unary.UnaryOp;
import json.execute.entity.ast.represents.object.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static json.execute.entity.Kind.IDENTIFIER;
import static json.execute.entity.ast.represents.binary.BinaryOp.*;
import static json.execute.entity.ast.represents.unary.UnaryOp.*;

public class Parser {

    // Cache some immutable stuff in global variables.
    public static final int APPLY_PRECEDENCE = 2;  // Function calls and indexing.
    public static final int UNARY_PRECEDENCE = 4;  // Logical and bitwise negation, unary + -
    public static final int PERCENT_PRECEDENCE = 5; // Modulo and string formatting
    public static final int MAX_PRECEDENCE = 15;   // Local, If, Import, Function, Error

    public static Map<BinaryOp, Integer> precedence_map = build_precedence_map();
    public static Map<String, UnaryOp> unary_map = build_unary_map();
    public static Map<String, BinaryOp> binary_map = build_binary_map();


    public static Map<BinaryOp, Integer> build_precedence_map() {
        Map<BinaryOp, Integer> r = new HashMap<BinaryOp, Integer>();

        r.put(BinaryOp.BOP_MULT, 5);
        r.put(BOP_DIV, 5);

        r.put(BOP_PLUS, 6);
        r.put(BOP_MINUS, 6);

        r.put(BOP_SHIFT_L, 7);
        r.put(BOP_SHIFT_R, 7);

        r.put(BOP_GREATER, 8);
        r.put(BOP_GREATER_EQ, 8);
        r.put(BOP_LESS, 8);
        r.put(BOP_LESS_EQ, 8);

        r.put(BOP_MANIFEST_EQUAL, 9);
        r.put(BOP_MANIFEST_UNEQUAL, 9);

        r.put(BOP_BITWISE_AND, 10);

        r.put(BOP_BITWISE_XOR, 11);

        r.put(BOP_BITWISE_OR, 12);

        r.put(BOP_AND, 13);

        r.put(BOP_OR, 14);

        return r;
    }

    public static Map<String, UnaryOp> build_unary_map() {
        Map<String, UnaryOp> r = new HashMap<String, UnaryOp>();
        r.put("!", UOP_NOT);
        r.put("~", UOP_BITWISE_NOT);
        r.put("+", UOP_PLUS);
        r.put("-", UOP_MINUS);
        return r;
    }

    public static Map<String, BinaryOp> build_binary_map() {
        Map<String, BinaryOp> r = new HashMap<String, BinaryOp>();

        r.put("*", BOP_MULT);
        r.put("/", BOP_DIV);

        r.put("+", BOP_PLUS);
        r.put("-", BOP_MINUS);

        r.put("<<", BOP_SHIFT_L);
        r.put(">>", BOP_SHIFT_R);

        r.put(">", BOP_GREATER);
        r.put(">=", BOP_GREATER_EQ);
        r.put("<", BOP_LESS);
        r.put("<=", BOP_LESS_EQ);

        r.put("==", BOP_MANIFEST_EQUAL);
        r.put("!=", BOP_MANIFEST_UNEQUAL);

        r.put("&", BOP_BITWISE_AND);
        r.put("^", BOP_BITWISE_XOR);
        r.put("|", BOP_BITWISE_OR);

        r.put("&&", BOP_AND);
        r.put("||", BOP_OR);
        return r;
    }

    public static boolean op_is_unary(String op, UnaryOp uop) {
        UnaryOp it = unary_map.get(op);
        if (it.equals(unary_map.get(unary_map.size()))) return false;
        uop = it;
        return true;
    }

    public static boolean op_is_binary(String op, BinaryOp bop) {
        BinaryOp it = binary_map.get(op);
        if (it.equals(binary_map.get(binary_map.size()))) return false;
        bop = it;
        return true;
    }

    public static LocationRange span(Token begin) {
        return new LocationRange(begin.getLocation().getFile(), begin.getLocation().getBegin(), begin.getLocation().getEnd());
    }

    public static LocationRange span(Token begin, Token end) {
        return new LocationRange(begin.getLocation().getFile(), begin.getLocation().getBegin(), end.getLocation().getEnd());
    }

    public static LocationRange span(Token begin, AST end) {
        return new LocationRange(begin.getLocation().getFile(), begin.getLocation().getBegin(), end.getLocation().getEnd());
    }

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Token popExpect(Kind k, String data) {
        Token tok = pop();
        if (tok.getKind() != k) {
            throw new RuntimeException("Expected token " + k + " but got " + tok);
        }
        if (data != null && tok.getData() != data) {
            throw new RuntimeException("Expected operator " + data + " but got " + tok.getData());
        }
        return tok;
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token parseCommaList(List<AST> exprs, Kind end, String element_kind, int obj_level) {
        boolean got_comma = true;
        do {
            Token next = peek();
            if (!got_comma) {
                if (next.getKind() == Kind.COMMA) {
                    pop();
                    next = peek();
                    got_comma = true;
                }
            }
            if (next.getKind() == end) {
                // got_comma can be true or false here.
                return pop();
            }
            if (!got_comma) {
                throw new RuntimeException("Expected a comma before next " + element_kind + ".");
            }
            exprs.add(parse(MAX_PRECEDENCE, obj_level));
            got_comma = false;
        } while (true);
    }

    public List<Identifier> parseIdentifierList(String element_kind, int obj_level) {
        List<AST> exprs = null;
        parseCommaList(exprs, Kind.PAREN_R, element_kind, obj_level);

        // Check they're all identifiers
        List<Identifier> ret = null;
        for (AST p_ast : exprs) {
            Var p = (Var) p_ast;
            if (p == null) {
                throw new RuntimeException("Not an identifier: " + p_ast);
            }
            ret.add(p.getId());
        }

        return ret;
    }

    public void parseBind(Map<Identifier, AST> binds, int obj_level) {
        Token var_id = popExpect(IDENTIFIER, null);
        Identifier id = new Identifier(var_id.getData());
        if (!binds.get(id).equals(binds.get(binds.size()))) {
            throw new RuntimeException("Duplicate local var: " + var_id.getData());
        }
        AST init;
        if (peek().getKind() == Kind.PAREN_L) {
            pop();
            List<Identifier> params = parseIdentifierList("function parameter", obj_level);
            popExpect(Kind.OPERATOR, "=");
            AST body = parse(MAX_PRECEDENCE, obj_level);
            init = new Function(span(var_id, body), params, body);
        } else {
            popExpect(Kind.OPERATOR, "=");
            init = parse(MAX_PRECEDENCE, obj_level);
        }
        binds.put(id, init);
    }

    private Token parseObjectRemainder(AST obj, Token tok, int obj_level) {
        List<String> literal_fields = new ArrayList<String>();
        List<Field> fields = null;
        Map<Identifier, AST> let_binds = new HashMap<Identifier, AST>();

        if (obj_level == 0) {
            Identifier hidden_var = new Identifier("$");
            let_binds.put(hidden_var, new Self(new LocationRange()));
        }

        boolean got_comma = true;

        // This is used to prevent { [x]: x, local foo = 3 for x in [1,2,3] }
        boolean last_was_local = false;
        do {

            Token next = pop();
            if (!got_comma) {
                if (next.getKind() == Kind.COMMA) {
                    next = pop();
                    got_comma = true;
                }
            }
            if (next.getKind() == Kind.BRACE_R) {
                // got_comma can be true or false here.
                List<Field> r = null;
                if (let_binds.size() == 0) {
                    r = fields;
                } else {
                    for (Field f : fields) {
                        AST body = new Local(f.getBody().getLocation(), let_binds, f.getBody());
                        r.add(0, new Field(f.getName(), f.getHide(), body));
                    }
                }
                obj = new ObjectConstructor(span(tok, next), r);
                return next;
            } else if (next.getKind() == Kind.FOR) {
                if (fields.size() != 1) {
                    throw new RuntimeException("Object composition can only have one field/value pair.");
                }
                if (last_was_local) {
                    throw new RuntimeException("Locals must appear first in an object comprehension.");
                }
                AST field = fields.get(0).getName();
                Hide field_hide = fields.get(0).getHide();
                AST value = fields.get(0).getBody();
                if (let_binds.size() > 0) {
                    value = new Local(value.getLocation(), let_binds, value);
                }
                if (field_hide != Hide.INHERIT) {
                    throw new RuntimeException("Object comprehensions cannot have hidden fields.");
                }
                if (got_comma) {
                    throw new RuntimeException("Unexpected comma before for.");
                }
                Token id_tok = popExpect(IDENTIFIER, null);
                Identifier id = new Identifier(id_tok.getData());
                popExpect(Kind.IN, null);
                AST array = parse(MAX_PRECEDENCE, obj_level);
                Token last = popExpect(Kind.BRACE_R, null);
                obj = new ObjectComposition(span(tok, last), field, value, id, array);
                return last;
            }
            if (!got_comma)
                throw new RuntimeException("Expected a comma before next field.");

            switch (next.getKind()) {
                case IDENTIFIER:
                case STRING: {
                    last_was_local = false;
                    boolean is_method = false;
                    List<Identifier> params = null;
                    if (peek().getKind() == Kind.PAREN_L) {
                        pop();
                        params = parseIdentifierList("method parameter", obj_level);
                        is_method = true;
                    }

                    boolean plus_sugar = false;
                    LocationRange plus_loc = null;
                    if (peek().getKind() == Kind.OPERATOR && peek().getData() == "+") {
                        plus_loc = peek().getLocation();
                        plus_sugar = true;
                        pop();
                    }

                    popExpect(Kind.COLON, null);
                    Hide field_hide = Hide.INHERIT;
                    if (peek().getKind() == Kind.COLON) {
                        pop();
                        field_hide = Hide.HIDDEN;
                        if (peek().getKind() == Kind.COLON) {
                            pop();
                            field_hide = Hide.VISIBLE;
                        }
                    }
                    if (literal_fields.contains(next.getData())) {
                        throw new RuntimeException("Duplicate field: " + next.getData());
                    }
                    AST field_expr = new LiteralString(next.getLocation(), next.getData());

                    AST body = parse(MAX_PRECEDENCE, obj_level + 1);
                    if (is_method) {
                        body = new Function(body.getLocation(), params, body);
                    }
                    if (plus_sugar) {
                        AST f = new LiteralString (plus_loc, next.getData());
                        AST super_f = new Index(plus_loc, new Super (new LocationRange()), f)
                        ;
                        body = new Binary (body.getLocation(), super_f, BOP_PLUS, body);
                    }
                    fields.add(0, new Field(field_expr, field_hide, body));
                }
                break;

                case LOCAL: {
                    last_was_local = true;
                    parseBind(let_binds, obj_level);
                }
                break;

                case BRACKET_L: {
                    last_was_local = false;
                    AST field_expr = parse(MAX_PRECEDENCE, obj_level);
                    popExpect(Kind.BRACKET_R, null);
                    popExpect(Kind.COLON, null);
                    Hide field_hide = Hide.INHERIT;
                    if (peek().getKind() == Kind.COLON) {
                        pop();
                        field_hide = Hide.HIDDEN;
                        if (peek().getKind() == Kind.COLON) {
                            pop();
                            field_hide = Hide.VISIBLE;
                        }
                    }
                    fields.add(0, new Field(field_expr, field_hide,
                            parse(MAX_PRECEDENCE, obj_level + 1)));
                }
                break;

                default:
                    throw new RuntimeException("parsing field definition");
            }

            got_comma = false;
        } while (true);
    }




    private List<Token> tokens;

    public Token peek() {
        return tokens.get(0);
    }

    public Token pop() {
        Token token = peek();
        tokens.remove(0);
        return token;
    }

    public AST parse(int precedence, int obj_level) {
        Token begin = peek();
        switch (begin.getKind()) {
            default:
                if (begin.getKind() == Kind.OPERATOR) {
                    UnaryOp uop = null;
                    if (UNARY_PRECEDENCE == precedence) {
                        Token op = pop();
                        AST expr = parse(precedence, obj_level);
                        return new Unary(new LocationRange(), uop, expr);
                    }
                }

                if (precedence == 0) {
                    return parseTerminal(obj_level);
                }

                AST lhs = parse(precedence - 1, obj_level);
                return null;
        }
    }

    private AST parseTerminal(int obj_level) {
        Token tok = pop();
        switch (tok.getKind()) {
            case BRACE_R:
            case BRACKET_R:
            case COLON:
            case COMMA:
            case DOT:
            case ELSE:
            case ERROR:
            case FOR:
            case FUNCTION:
            case IF:
            case IN:
            case LOCAL:
            case OPERATOR:
            case PAREN_R:
            case SEMICOLON:
            case TAILCALL:
            case THEN:
                throw new RuntimeException("parsing terminal");

            case END_OF_FILE:
                throw new RuntimeException("Unexpected end of file.");

            case BRACE_L: {
                AST obj = null;
                parseObjectRemainder(obj, tok, obj_level);
                return obj;
            }
        }
        return null;
    }

    public List<Token> getTokens() {
        return tokens;
    }


    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
}
