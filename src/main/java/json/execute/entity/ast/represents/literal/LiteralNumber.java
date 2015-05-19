package json.execute.entity.ast.represents.literal;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class LiteralNumber extends AST {

    private double value;

    public LiteralNumber(LocationRange locationRange, double value) {
        super(locationRange, ASTType.AST_LITERAL_NUMBER);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
