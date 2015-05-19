package json.execute.entity.ast.represents.object;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;
import json.execute.entity.ast.Identifier;

public class ObjectComposition extends AST {

    private AST field;
    private AST value;
    private Identifier id;
    private AST array;

    public ObjectComposition(LocationRange locationRange, AST field, AST value, Identifier id, AST array) {
        super(locationRange, ASTType.AST_OBJECT_COMPOSITION);
        this.field = field;
        this.value = value;
        this.id = id;
        this.array = array;
    }

    public AST getField() {
        return field;
    }

    public void setField(AST field) {
        this.field = field;
    }

    public AST getValue() {
        return value;
    }

    public void setValue(AST value) {
        this.value = value;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public AST getArray() {
        return array;
    }

    public void setArray(AST array) {
        this.array = array;
    }
}
