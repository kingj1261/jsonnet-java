package json.execute.entity.ast.represents.literal;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class LiteralBoolean extends AST {

    private boolean value;

    public LiteralBoolean(LocationRange locationRange, boolean value) {
        super(locationRange, ASTType.AST_LITERAL_BOOLEAN);
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
