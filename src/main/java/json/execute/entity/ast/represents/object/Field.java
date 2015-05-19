package json.execute.entity.ast.represents.object;

import json.execute.entity.ast.AST;

public class Field {

    private AST name;
    private Hide hide;
    private AST body;

    public Field(AST name, Hide hide, AST body) {
        this.name = name;
        this.hide = hide;
        this.body = body;
    }

    public AST getBody() {
        return body;
    }

    public AST getName() {
        return name;
    }

    public Hide getHide() {
        return hide;
    }

    public void setBody(AST body) {
        this.body = body;
    }

    public void setHide(Hide hide) {
        this.hide = hide;
    }

    public void setName(AST name) {
        this.name = name;
    }

}
