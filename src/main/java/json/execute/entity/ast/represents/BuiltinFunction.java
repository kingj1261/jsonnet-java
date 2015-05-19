package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;
import json.execute.entity.ast.Identifier;

import java.util.List;

public class BuiltinFunction extends AST {

    private long id;
    private List<Identifier> params;

    public BuiltinFunction(LocationRange locationRange, long id, List<Identifier> params) {
        super(locationRange, ASTType.AST_BUILTIN_FUNCTION);
        this.id = id;
        this.params = params;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Identifier> getParams() {
        return params;
    }

    public void setParams(List<Identifier> params) {
        this.params = params;
    }
}
