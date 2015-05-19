package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;
import json.execute.entity.ast.Identifier;

public class Var extends AST {

    private Identifier id;
    private Identifier original;

    public Var(LocationRange locationRange, Identifier id) {
        super(locationRange, ASTType.AST_VAR);
        this.id = id;
        this.original = id;
    }

    public Var(LocationRange locationRange, Identifier id, Identifier original) {
        super(locationRange, ASTType.AST_VAR);
        this.id = id;
        this.original = original;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Identifier getOriginal() {
        return original;
    }

    public void setOriginal(Identifier original) {
        this.original = original;
    }
}
