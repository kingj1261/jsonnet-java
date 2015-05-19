package json.execute.entity.ast.represents.unary;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;
import json.execute.entity.ast.represents.unary.UnaryOp;

public class Unary extends AST {

    private UnaryOp op;
    private AST expr;

    public Unary(LocationRange locationRange, UnaryOp uop, AST expr) {
        super(locationRange, ASTType.AST_UNARY);
        this.op = uop;
        this.expr = expr;
    }

    public AST getExpr() {
        return expr;
    }

    public UnaryOp getOp() {
        return op;
    }

    public void setExpr(AST expr) {
        this.expr = expr;
    }

    public void setOp(UnaryOp op) {
        this.op = op;
    }

}
