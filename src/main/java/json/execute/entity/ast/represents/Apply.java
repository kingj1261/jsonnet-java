package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

import java.util.List;

public class Apply extends AST {

    private AST target;
    private List<AST> arguments;
    boolean tailcall;

    public Apply(LocationRange locationRange, AST target, List<AST> arguments, boolean tailcall) {
        super(locationRange, ASTType.AST_APPLY);
        this.target = target;
        this.arguments = arguments;
        this.tailcall = tailcall;
    }

    public void setTarget(AST target) {
        this.target = target;
    }

    public void setArguments(List<AST> arguments) {
        this.arguments = arguments;
    }

    public void setTailcall(boolean tailcall) {
        this.tailcall = tailcall;
    }

    public AST getTarget() {
        return target;
    }

    public List<AST> getArguments() {
        return arguments;
    }

    public boolean isTailcall() {
        return tailcall;
    }
}
