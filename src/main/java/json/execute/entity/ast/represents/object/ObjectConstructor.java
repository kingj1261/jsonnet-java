package json.execute.entity.ast.represents.object;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

import java.util.List;

public class ObjectConstructor extends AST {

    private List<Field> fields;

    public ObjectConstructor(LocationRange locationRange, List<Field> fields) {
        super(locationRange, ASTType.AST_OBJECT);
        this.fields = fields;
    }

    public ObjectConstructor() {

    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
