package json.execute.entity.ast.represents.importfile;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.ASTType;

public class Importstr extends AST {

    private String file;

    public Importstr(LocationRange locationRange, String file) {
        super(locationRange, ASTType.AST_IMPORTSTR);
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}