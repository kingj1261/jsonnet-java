package json.execute.entity.ast.represents.literal;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class LiteralString extends AST {

    private String value;

    public LiteralString(LocationRange locationRange, String value) {
        super(locationRange, ASTType.AST_LITERAL_STRING);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
