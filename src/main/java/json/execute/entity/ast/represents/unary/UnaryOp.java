package json.execute.entity.ast.represents.unary;

public enum UnaryOp {
    UOP_NOT,
    UOP_BITWISE_NOT,
    UOP_PLUS,
    UOP_MINUS;

    @Override
    public String toString() {
        switch (this) {
            case UOP_PLUS:
                return "+";
            case UOP_MINUS:
                return "-";
            case UOP_BITWISE_NOT:
                return "~";
            case UOP_NOT:
                return "!";

            default:
                throw new RuntimeException("INTERNAL ERROR: Unrecognised unary operator");
        }
    }
}
