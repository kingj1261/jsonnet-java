package json.execute.entity.ast.represents;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

import java.util.List;

public class Array extends AST {

    private List<AST> elements;

    public Array(LocationRange locationRange, List<AST> elements) {
        super(locationRange, ASTType.AST_ARRAY);
        this.elements = elements;
    }

    public List<AST> getElements() {
        return elements;
    }

    public void setElements(List<AST> elements) {
        this.elements = elements;
    }
}
