package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;
import json.execute.entity.ast.Identifier;

import java.util.Map;

public class Local extends AST {

    private Map<Identifier, AST> binds;
    private AST body;

    public Local(LocationRange locationRange, Map<Identifier, AST> binds, AST body) {
        super(locationRange, ASTType.AST_LOCAL);
        this.binds = binds;
        this.body = body;
    }

    public Map<Identifier, AST> getBinds() {
        return binds;
    }

    public void setBinds(Map<Identifier, AST> binds) {
        this.binds = binds;
    }

    public AST getBody() {
        return body;
    }

    public void setBody(AST body) {
        this.body = body;
    }
}
