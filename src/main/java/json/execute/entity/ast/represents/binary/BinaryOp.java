package json.execute.entity.ast.represents.binary;

public enum BinaryOp {
    BOP_MULT,
    BOP_DIV,

    BOP_PLUS,
    BOP_MINUS,

    BOP_SHIFT_L,
    BOP_SHIFT_R,

    BOP_GREATER,
    BOP_GREATER_EQ,
    BOP_LESS,
    BOP_LESS_EQ,

    BOP_MANIFEST_EQUAL,
    BOP_MANIFEST_UNEQUAL,

    BOP_BITWISE_AND,
    BOP_BITWISE_XOR,
    BOP_BITWISE_OR,

    BOP_AND,
    BOP_OR;

    @Override
    public String toString() {
        switch (this) {
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
                throw new RuntimeException("INTERNAL ERROR: Unrecognised binary operator");
        }
    }
}
