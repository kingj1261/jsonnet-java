package json.execute.entity.ast.represents.binary;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Binary extends AST {

    private AST left;
    private BinaryOp op;
    private AST right;

    public Binary(LocationRange locationRange, AST left, BinaryOp op, AST right) {
        super(locationRange, ASTType.AST_BINARY);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public AST getLeft() {
        return left;
    }

    public void setLeft(AST left) {
        this.left = left;
    }

    public BinaryOp getOp() {
        return op;
    }

    public void setOp(BinaryOp op) {
        this.op = op;
    }

    public AST getRight() {
        return right;
    }

    public void setRight(AST right) {
        this.right = right;
    }
}
