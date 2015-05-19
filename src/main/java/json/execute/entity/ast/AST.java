package json.execute.entity.ast;

import json.execute.entity.LocationRange;

import java.util.List;

public class AST {

    private LocationRange location;
    private ASTType type;
    private List<Identifier> freeVariables;

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

    public List<Identifier> getFreeVariables() {
        return freeVariables;
    }

    public void setFreeVariables(List<Identifier> freeVariables) {
        this.freeVariables = freeVariables;
    }

}
