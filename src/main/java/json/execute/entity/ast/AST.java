package json.execute.entity.ast;

import json.execute.entity.LocationRange;

public class AST {

    private LocationRange location;
    private ASTType type;

    public AST(LocationRange locationRange, ASTType astUnary) {
        this.location = locationRange;
        this.type = astUnary;
    }

    public LocationRange getLocation() {
        return location;
    }

    public ASTType getType() {
        return type;
    }

    public void setLocation(LocationRange location) {
        this.location = location;
    }

    public void setType(ASTType type) {
        this.type = type;
    }

}
