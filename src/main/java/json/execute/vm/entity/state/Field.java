package json.execute.vm.entity.state;

import json.execute.entity.ast.AST;
import json.execute.entity.ast.represents.object.Hide;

public class Field {

    private Hide hide;
    private AST body;

    public Hide getHide() {
        return hide;
    }

    public void setHide(Hide hide) {
        this.hide = hide;
    }

    public AST getBody() {
        return body;
    }

    public void setBody(AST body) {
        this.body = body;
    }
}
