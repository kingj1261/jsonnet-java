package json.execute.parser;

import json.execute.Executor;
import json.execute.entity.*;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.Identifier;
import json.execute.entity.ast.represents.*;
import json.execute.entity.ast.represents.Error;
import json.execute.entity.ast.represents.binary.Binary;
import json.execute.entity.ast.represents.binary.BinaryOp;
import json.execute.entity.ast.represents.importfile.Import;
import json.execute.entity.ast.represents.importfile.Importstr;
import json.execute.entity.ast.represents.literal.LiteralBoolean;
import json.execute.entity.ast.represents.literal.LiteralNull;
import json.execute.entity.ast.represents.literal.LiteralNumber;
import json.execute.entity.ast.represents.literal.LiteralString;
import json.execute.entity.ast.represents.object.Hide;
import json.execute.entity.ast.represents.object.ObjectComposition;
import json.execute.entity.ast.represents.object.ObjectConstructor;
import json.execute.entity.ast.represents.unary.Unary;
import json.execute.entity.ast.represents.unary.UnaryOp;
import json.execute.entity.ast.represents.object.Field;
import json.execute.lex.Lexer;

import java.io.IOException;
import java.nio.charset.Charset;
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
            throw new RuntimeException("Expected token " + k + " but got " + tok.getKind());
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

    private Map<Token, AST> parseObjectRemainder(AST obj, Token tok, int obj_level) {
        List<String> literal_fields = new ArrayList<String>();
        List<Field> fields = new ArrayList<Field>();
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
                List<Field> r = new ArrayList<Field>();
                if (let_binds.size() == 0) {
                    r = fields;
                } else {
                    for (Field f : fields) {
                        AST body = new Local(f.getBody().getLocation(), let_binds, f.getBody());
                        r.add(0, new Field(f.getName(), f.getHide(), body));
                    }
                }
//                ObjectConstructor objectConstructor = new ObjectConstructor(span(tok, next), r);
                obj = new ObjectConstructor(span(tok, next), r);
                Map<Token, AST> result = new HashMap<Token, AST>();
                result.put(next, obj);
                return result;
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
                return null;
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
                        AST f = new LiteralString(plus_loc, next.getData());
                        AST super_f = new Index(plus_loc, new Super(new LocationRange()), f);
                        body = new Binary(body.getLocation(), super_f, BOP_PLUS, body);
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
                ObjectConstructor obj = new ObjectConstructor();
                Map<Token, AST> tokenASTMap = parseObjectRemainder(obj, tok, obj_level);
                for (Map.Entry entry : tokenASTMap.entrySet()) {
                    obj = (ObjectConstructor) entry.getValue();
                }
                return obj;
            }
            case BRACKET_L: {
                Token next = peek();
                if (next.getKind() == Kind.BRACKET_R) {
                    pop();
                    return new Array(span(tok, next), new ArrayList<AST>());
                }
                AST first = parse(MAX_PRECEDENCE, obj_level);
                next = peek();
                if (next.getKind() == Kind.FOR) {
                    LocationRange l = new LocationRange();
                    pop();
                    Token id_token = popExpect(Kind.IDENTIFIER, null);
                    Identifier id = new Identifier(id_token.getData());
                    List<Identifier> params = new ArrayList<Identifier>();
                    params.add(id);
                    AST std = new Var(l, new Identifier("std"));
                    AST map_func = new Function(first.getLocation(), params, first);
                    popExpect(Kind.IN, null);
                    AST arr = parse(MAX_PRECEDENCE, obj_level);
                    Token maybe_if = pop();
                    if (maybe_if.getKind() == Kind.BRACKET_R) {
                        AST map_str = new LiteralString(l, "map");
                        AST map = new Index(l, std, map_str);
                        List<AST> args = new ArrayList<AST>();
                        args.add(map_func);
                        args.add(arr);
                        return new Apply(span(tok, maybe_if), map, args, false);
                    } else if (maybe_if.getKind() == Kind.IF) {
                        AST cond = parse(MAX_PRECEDENCE, obj_level);
                        Token last = popExpect(Kind.BRACKET_R, null);
                        AST filter_func = new Function(cond.getLocation(), params, cond);
                        AST fmap_str = new LiteralString(l, "filterMap");
                        AST fmap = new Index(l, std, fmap_str);
                        List<AST> args = new ArrayList<AST>();
                        args.add(filter_func);
                        args.add(map_func);
                        args.add(arr);
                        return new Apply(span(tok, last), fmap, args, false);
                    } else {
                        throw new RuntimeException("Expected if or ] after for clause, got: " + maybe_if);
                    }
                } else {
                    List<AST> elements = new ArrayList<AST>();
                    elements.add(first);
                    do {
                        next = peek();
                        boolean got_comma = false;
                        if (next.getKind() == Kind.COMMA) {
                            pop();
                            next = peek();
                            got_comma = true;
                        }
                        if (next.getKind() == Kind.BRACKET_R) {
                            pop();
                            break;
                        }
                        if (!got_comma) {
                            throw new RuntimeException("Expected a comma before next array element.");
                        }
                        elements.add(parse(MAX_PRECEDENCE, obj_level));
                    } while (true);
                    return new Array(span(tok, next), elements);
                }
            }

            case PAREN_L: {
                AST inner = parse(MAX_PRECEDENCE, obj_level);
                popExpect(Kind.PAREN_R, null);
                return inner;
            }


            // Literals
            case NUMBER:
                return new LiteralNumber(span(tok), Double.valueOf(tok.getData()));

            case STRING:
                return new LiteralString(span(tok), tok.getData());

            case FALSE:
                return new LiteralBoolean(span(tok), false);

            case TRUE:
                return new LiteralBoolean(span(tok), true);

            case NULL_LIT:
                return new LiteralNull(span(tok));

            // Import
            case IMPORT: {
                Token file = popExpect(Kind.STRING, null);
                return new Import(span(tok, file), file.getData());
            }

            case IMPORTSTR: {
                Token file = popExpect(Kind.STRING, null);
                return new Importstr(span(tok, file), file.getData());
            }


            // Variables
            case DOLLAR:
                if (obj_level == 0) {
                    throw new RuntimeException("No top-level object found.");
                }
                return new Var(span(tok), new Identifier("$"));

            case IDENTIFIER:
                return new Var(span(tok), new Identifier(tok.getData()));

            case SELF:
                return new Self(span(tok));

            case SUPER:
                return new Super(span(tok));
        }

        throw new RuntimeException("INTERNAL ERROR: Unknown tok kind: " + tok.getKind());
    }

    public AST parse(int precedence, int obj_level) {
        Token begin = peek();

        switch (begin.getKind()) {

            // These cases have effectively MAX_PRECEDENCE as the first
            // call to parse will parse them.
            case ERROR: {
                pop();
                AST expr = parse(MAX_PRECEDENCE, obj_level);
                return new Error(span(begin, expr), expr);
            }

            case IF: {
                pop();
                AST cond = parse(MAX_PRECEDENCE, obj_level);
                popExpect(Kind.THEN, null);
                AST branch_true = parse(MAX_PRECEDENCE, obj_level);
                AST branch_false;
                if (peek().getKind() == Kind.ELSE) {
                    pop();
                    branch_false = parse(MAX_PRECEDENCE, obj_level);
                } else {
                    branch_false = new LiteralNull(span(begin, branch_true));
                }
                return new Conditional(span(begin, branch_false),
                        cond, branch_true, branch_false);
            }

            case FUNCTION: {
                pop();
                Token next = pop();
                if (next.getKind() == Kind.PAREN_L) {
                    List<AST> params_asts = null;
                    parseCommaList(params_asts, Kind.PAREN_R,
                            "function parameter", obj_level);
                    AST body = parse(MAX_PRECEDENCE, obj_level);
                    List<Identifier> params = new ArrayList<Identifier>();
                    for (AST p_ast : params_asts) {
                        Var p = (Var) p_ast;
                        if (p == null) {
                            throw new RuntimeException("Not an identifier: " + p_ast);
                        }
                        params.add(p.getId());
                    }
                    return new Function(span(begin, body), params, body);
                } else {
                    throw new RuntimeException("Expected ( but got " + next);
                }
            }

            case LOCAL: {
                pop();
                Map<Identifier, AST> binds = null;
                do {
                    parseBind(binds, obj_level);
                    Token delim = pop();
                    if (delim.getKind() != Kind.SEMICOLON && delim.getKind() != Kind.COMMA) {
                        throw new RuntimeException("Expected , or ; but got " + delim);
                    }
                    if (delim.getKind() == Kind.SEMICOLON) break;
                } while (true);
                AST body = parse(MAX_PRECEDENCE, obj_level);
                return new Local(span(begin, body), binds, body);
            }

            default:

                // Unary operator.
                if (begin.getKind() == Kind.OPERATOR) {
                    UnaryOp uop = null;
                    if (!op_is_unary(begin.getData(), uop)) {
                        throw new RuntimeException("Not a unary operator: " + begin.getData());
                    }
                    if (UNARY_PRECEDENCE == precedence) {
                        Token op = pop();
                        AST expr = parse(precedence, obj_level);
                        return new Unary(span(op, expr), uop, expr);
                    }
                }

                // Base case
                if (precedence == 0) return parseTerminal(obj_level);

                AST lhs = parse(precedence - 1, obj_level);

                while (true) {

                    // Then next token must be a binary operator.

                    // The compiler can't figure out that this is never used uninitialized.
                    BinaryOp bop = BOP_PLUS;

                    // Check precedence is correct for this level.  If we're
                    // parsing operators with higher precedence, then return
                    // lhs and let lower levels deal with the operator.
                    switch (peek().getKind()) {
                        // Logical / arithmetic binary operator.
                        case OPERATOR:
                            if (peek().getData() == "%") {
                                if (PERCENT_PRECEDENCE != precedence) return lhs;
                            } else {
                                if (!op_is_binary(peek().getData(), bop)) {
                                    throw new RuntimeException("Not a binary operator: " + peek().getData());
                                }
                                if (precedence_map.get(bop) != precedence) return lhs;
                            }
                            break;

                        // Index, Apply
                        case DOT:
                        case BRACKET_L:
                        case PAREN_L:
                        case BRACE_L:
                            if (APPLY_PRECEDENCE != precedence) return lhs;
                            break;

                        default:
                            return lhs;
                    }

                    Token op = pop();
                    if (op.getKind() == Kind.BRACKET_L) {
                        AST index = parse(MAX_PRECEDENCE, obj_level);
                        Token end = popExpect(Kind.BRACKET_R, null);
                        lhs = new Index(span(begin, end), lhs, index);

                    } else if (op.getKind() == Kind.DOT) {
                        Token field = popExpect(Kind.IDENTIFIER, null);
                        AST index = new LiteralString(span(field), field.getData());
                        lhs = new Index(span(begin, field), lhs, index);

                    } else if (op.getKind() == Kind.PAREN_L) {
                        List<AST> args = null;
                        Token end = parseCommaList(args, Kind.PAREN_R,
                                "function argument", obj_level);
                        boolean tailcall = false;
                        if (peek().getKind() == Kind.TAILCALL) {
                            pop();
                            tailcall = true;
                        }
                        lhs = new Apply(span(begin, end), lhs, args, tailcall);

                    } else if (op.getKind() == Kind.BRACE_L) {
                        AST obj = null;
                        Token end = (Token) parseObjectRemainder(obj, op, obj_level);
                        lhs = new Binary(span(begin, end), lhs, BOP_PLUS, obj);

                    } else if (op.getData() == "%") {
                        AST rhs = parse(precedence - 1, obj_level);
                        LocationRange l = null;
                        AST std = new Var(l, new Identifier("std"));
                        AST mod_str = new LiteralString(l, "mod");
                        AST f_mod = new Index(l, std, mod_str);
                        List<AST> args = new ArrayList<AST>();
                        args.add(lhs);
                        args.add(rhs);
                        lhs = new Apply(span(begin, rhs), f_mod, args, false);

                    } else {
                        // Logical / arithmetic binary operator.
                        AST rhs = parse(precedence - 1, obj_level);
                        boolean invert = false;
                        if (bop == BOP_MANIFEST_UNEQUAL) {
                            bop = BOP_MANIFEST_EQUAL;
                            invert = true;
                        }
                        lhs = new Binary(span(begin, rhs), lhs, bop, rhs);
                        if (invert) {
                            lhs = new Unary(lhs.getLocation(), UOP_NOT, lhs);
                        }
                    }
                }
        }
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

    public List<Token> getTokens() {
        return tokens;
    }


    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    static long max_builtin = 23;

    public static AST do_parse(String file, String input) {
        // Lex the input.
        Lexer lexer = new Lexer();
        List<Token> token_list = lexer.jsonnet_lex(file, input);

        // Parse the input.
        Parser parser = new Parser(token_list);
        AST expr = parser.parse(MAX_PRECEDENCE, 0);
        if (token_list.get(0).getKind() != Kind.END_OF_FILE) {
            throw new RuntimeException("Did not expect: " + token_list.get(0).getKind());
        }

        return expr;
    }

    public static AST jsonnet_parse(String file, String input) throws IOException {
        // Parse the actual file.
        AST expr = do_parse(file, input);

        // Now, implement the std library by wrapping in a local construct.
        String std = Executor.readFile("", Charset.defaultCharset());
//        ObjectConstructor std_obj = (ObjectConstructor) do_parse("std.jsonnet", std);

        // For generated ASTs, use a bogus location.
        LocationRange l;

//        Bind 'std' builtins that are implemented natively.
//        Object::Fields &fields = std_obj->fields;
//        for (unsigned long c=0 ; c <= max_builtin ; ++c) {
//        const auto &decl = jsonnet_builtin_decl(c);
//        std::vector<const Identifier*> params;
//        for (const auto &p : decl.params)
//        params.push_back(alloc->makeIdentifier(p));
//        fields.emplace_back(alloc->make<LiteralString>(l, decl.name), Object::Field::HIDDEN,
//                alloc->make<BuiltinFunction>(l, c, params));
//    }
//
//        Local::Binds std_binds;
//        std_binds[alloc->makeIdentifier("std")] = std_obj;
        AST wrapped = new Local(expr.getLocation(), expr);
        return wrapped;
    }

    public static String jsonnet_unparse_jsonnet(AST ast) {
        Local wrapper = (Local) ast;
        if (wrapper == null) {
            throw new RuntimeException("INTERNAL ERROR: Unparsing an AST that wasn't wrapped in a std local.");
        }
        return unparse(wrapper.getBody());
    }

    public static String unparse(AST ast_) {
        if (ast_ instanceof Apply) {
            Apply ast = (Apply) ast_;
            System.out.print(unparse(ast.getTarget()));
            if (ast.getArguments().size() == 0) {
                System.out.print("()");
            } else {
                String prefix = "(";
                for (AST arg : ast.getArguments()) {
                    System.out.print(prefix + unparse(arg));
                    prefix = ", ";
                }
                System.out.print(")");
            }

        } else if (ast_ instanceof Array) {
            Array ast = (Array) ast_;
            if (ast.getElements().size() == 0) {
                System.out.print("[ ]");
            } else {
                String prefix = "[";
                for (AST element : ast.getElements()) {
                    System.out.print(prefix);
                    System.out.print(unparse(element));
                    prefix = ", ";
                }
                System.out.print("]");
            }

        } else if (ast_ instanceof Binary) {
            Binary ast = (Binary) ast_;
            System.out.print(unparse(ast.getLeft()) + " " + bop_string(ast.getOp()) + " " + unparse(ast.getRight()));

        } else if (ast_ instanceof BuiltinFunction) {
            throw new RuntimeException("INTERNAL ERROR: Unparsing builtin function.");
        } else if (ast_ instanceof Conditional) {
            Conditional ast = (Conditional) ast_;
            System.out.print("if " + unparse(ast.getCond()) + " then " + unparse(ast.getBranchTrue()) + " else " + unparse(ast.getBranchFalse()));

        } else if (ast_ instanceof Error) {
            Error ast = (Error) ast_;
            System.out.print("error " + unparse(ast.getExpr()));

        } else if (ast_ instanceof Function) {
            Function ast = (Function) ast_;
            System.out.print("function ");
            String prefix = "(";
            for (Identifier arg : ast.getParameters()) {
                System.out.print(prefix + arg.getName());
                prefix = ", ";
            }
            System.out.print(") " + unparse(ast.getBody()));

        } else if (ast_ instanceof Import) {
            Import ast = (Import) ast_;
            System.out.print("import " + jsonnet_unparse_escape(ast.getFile()));

        } else if (ast_ instanceof Importstr) {
            Importstr ast = (Importstr) ast_;
            System.out.print("importstr " + jsonnet_unparse_escape(ast.getFile()));

        } else if (ast_ instanceof Index) {
            Index ast = (Index) ast_;
            System.out.println(unparse(ast.getTarget()) + "[" + unparse(ast.getIndex()) + "]");

        } else if (ast_ instanceof Local) {
            Local ast = (Local) ast_;
            String prefix = "local ";
            for (Map.Entry bind : ast.getBinds().entrySet()) {
                System.out.print(prefix + ((Identifier) bind.getKey()).getName() + " = " + unparse((AST) bind.getValue()));
                prefix = ", ";
            }
            System.out.print("; " + unparse(ast.getBody()));

        } else if (ast_ instanceof LiteralBoolean) {
            LiteralBoolean ast = (LiteralBoolean) ast_;
            System.out.print(ast.isValue());

        } else if (ast_ instanceof LiteralNumber) {
            LiteralNumber ast = (LiteralNumber) ast_;
            System.out.println(ast.getValue());

        } else if (ast_ instanceof LiteralString) {
            LiteralString ast = (LiteralString) ast_;
            System.out.print(jsonnet_unparse_escape(ast.getValue()));

        } else if (ast_ instanceof LiteralNull) {
            System.out.print("null");

        } else if (ast_ instanceof ObjectConstructor) {
            ObjectConstructor ast = (ObjectConstructor) ast_;
            if (ast.getFields().size() == 0) {
                System.out.print("{ }");
            } else {
                String prefix = "{";
                for (Field f : ast.getFields()) {
                    System.out.print(prefix);
                    String colons = null;
                    switch (f.getHide()) {
                        case INHERIT:
                            colons = ":";
                            break;
                        case HIDDEN:
                            colons = "::";
                            break;
                        case VISIBLE:
                            colons = ":::";
                            break;
                        default:
                            throw new RuntimeException("INTERNAL ERROR: Unknown FieldHide: " + f.getHide());
                    }
                    System.out.print("[" + unparse(f.getName()) + "]" + colons + " " + unparse(f.getBody()));
                    prefix = ", ";
                }
                System.out.print("}");
            }

        } else if (ast_ instanceof ObjectComposition) {
            ObjectComposition ast = (ObjectComposition) ast_;
            System.out.print("{[" + unparse(ast.getField()) + "]: " + unparse(ast.getValue()));
            System.out.print(" for " + ast.getId().getName() + " in " + unparse(ast.getArray()));
            System.out.print("}");

        } else if (ast_ instanceof Self) {
            System.out.println("self");

        } else if (ast_ instanceof Super) {
            System.out.println("super");

        } else if (ast_ instanceof Unary) {
            Unary ast = (Unary) ast_;
            System.out.print(uop_string(ast.getOp()) + unparse(ast.getExpr()));

        } else if (ast_ instanceof Var) {
            Var ast = (Var) ast_;
            System.out.print(ast.getId().getName());

        } else {
            throw new RuntimeException("INTERNAL ERROR: Unknown AST: " + ast_);
        }

        return "(" + "" + ")";
    }

    public static String jsonnet_unparse_escape(String str) {
        System.out.print('\"');
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    System.out.print("\\\"");
                    break;
                case '\\':
                    System.out.print("\\\\");
                    break;
                case '\b':
                    System.out.print("\\b");
                    break;
                case '\f':
                    System.out.print("\\f");
                    break;
                case '\n':
                    System.out.print("\\n");
                    break;
                case '\r':
                    System.out.print("\\r");
                    break;
                case '\t':
                    System.out.print("\\t");
                    break;
                case '\0':
                    System.out.print("\\u0000");
                    break;
                default: {
                    if (c < 0x20 || c > 0x7e) {
                        //Unprintable, use
                        System.out.print("");
                    } else {
                        // Printable, write verbatim
                        System.out.print(c);
                    }
                }
            }
        }
        System.out.print('\"');
        return "";
    }

    public static String bop_string(BinaryOp bop) {
        switch (bop) {
            case BOP_MULT:
                return "*";
            case BOP_DIV:
                return "/";

            case BOP_PLUS:
                return "+";
            case BOP_MINUS:
                return "-";

            case BOP_SHIFT_L:
                return "<<";
            case BOP_SHIFT_R:
                return ">>";

            case BOP_GREATER:
                return ">";
            case BOP_GREATER_EQ:
                return ">=";
            case BOP_LESS:
                return "<";
            case BOP_LESS_EQ:
                return "<=";

            case BOP_MANIFEST_EQUAL:
                return "==";
            case BOP_MANIFEST_UNEQUAL:
                return "!=";

            case BOP_BITWISE_AND:
                return "&";
            case BOP_BITWISE_XOR:
                return "^";
            case BOP_BITWISE_OR:
                return "|";

            case BOP_AND:
                return "&&";
            case BOP_OR:
                return "||";

            default:
                throw new RuntimeException("INTERNAL ERROR: Unrecognised binary operator: " + bop);
        }
    }

    public static String uop_string(UnaryOp uop) {
        switch (uop) {
            case UOP_PLUS:
                return "+";
            case UOP_MINUS:
                return "-";
            case UOP_BITWISE_NOT:
                return "~";
            case UOP_NOT:
                return "!";

            default:
                throw new RuntimeException("INTERNAL ERROR: Unrecognised unary operator: " + uop);
        }
    }

}
