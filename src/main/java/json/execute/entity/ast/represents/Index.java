package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Index extends AST {

    private AST target;
    private AST index;

    public Index(LocationRange locationRange, AST target, AST index) {
        super(locationRange, ASTType.AST_INDEX);
        this.target = target;
        this.index = index;
    }

    public AST getTarget() {
        return target;
    }

    public void setTarget(AST target) {
        this.target = target;
    }

    public AST getIndex() {
        return index;
    }

    public void setIndex(AST index) {
        this.index = index;
    }
}
