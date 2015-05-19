package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;
import json.execute.entity.ast.Identifier;

import java.util.List;

public class Function extends AST {

    private List<Identifier> parameters;
    private AST body;

    public Function(LocationRange locationRange, List<Identifier> parameters, AST body) {
        super(locationRange, ASTType.AST_FUNCTION);
        this.parameters = parameters;
        this.body = body;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public void setParameters(List<Identifier> parameters) {
        this.parameters = parameters;
    }

    public AST getBody() {
        return body;
    }

    public void setBody(AST body) {
        this.body = body;
    }
}
