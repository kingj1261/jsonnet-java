package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Conditional extends AST {

    private AST cond;
    private AST branchTrue;
    private AST branchFalse;

    public Conditional(LocationRange locationRange, AST cond, AST branchTrue, AST branchFalse) {
        super(locationRange, ASTType.AST_CONDITIONAL);
        this.cond = cond;
        this.branchFalse = branchFalse;
        this.branchTrue = branchTrue;
    }

    public AST getCond() {
        return cond;
    }

    public void setCond(AST cond) {
        this.cond = cond;
    }

    public AST getBranchTrue() {
        return branchTrue;
    }

    public void setBranchTrue(AST branchTrue) {
        this.branchTrue = branchTrue;
    }

    public AST getBranchFalse() {
        return branchFalse;
    }

    public void setBranchFalse(AST branchFalse) {
        this.branchFalse = branchFalse;
    }
}
