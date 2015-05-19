package json.execute.entity.ast.represents.importfile;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Import extends AST {

    private String file;

    public Import(LocationRange locationRange, String file) {
        super(locationRange, ASTType.AST_IMPORT);
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
