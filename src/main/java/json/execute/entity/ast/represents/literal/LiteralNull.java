package json.execute.entity.ast.represents.literal;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class LiteralNull extends AST {

    public LiteralNull(LocationRange locationRange) {
        super(locationRange, ASTType.AST_LITERAL_NULL);
    }
}
