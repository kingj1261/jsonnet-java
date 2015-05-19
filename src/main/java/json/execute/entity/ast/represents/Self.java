package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Self extends AST {

    public Self(LocationRange locationRange) {
        super(locationRange, ASTType.AST_SELF);
    }

}
