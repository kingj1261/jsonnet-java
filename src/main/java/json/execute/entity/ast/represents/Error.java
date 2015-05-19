package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Error extends AST {

    private AST expr;

    public Error(LocationRange locationRange, AST expr) {
        super(locationRange, ASTType.AST_ERROR);
        this.expr = expr;
    }

    public AST getExpr() {
        return expr;
    }

    public void setExpr(AST expr) {
        this.expr = expr;
    }
}
