package json.execute.vm.entity.state.heap;

import json.execute.entity.ast.AST;
import json.execute.entity.ast.Identifier;
import json.execute.vm.entity.state.GarbageCollectionMark;

import java.util.List;
import java.util.Map;

public class HeapClosure extends HeapEntity {

    private Map<Identifier, HeapThunk> upValues;
    private HeapObject self;
    private int offset;
    private List<Identifier> params;
    private AST body;
    private long builtin;

    public HeapClosure(GarbageCollectionMark mark,
                       Map<Identifier, HeapThunk> upValues,
                       HeapObject self,
                       int offset,
                       List<Identifier> params,
                       AST body,
                       long builtin) {
        super(mark);
        this.upValues = upValues;
        this.self = self;
        this.offset = offset;
        this.params = params;
        this.body = body;
        this.builtin = builtin;
    }

    public Map<Identifier, HeapThunk> getUpValues() {
        return upValues;
    }

    public void setUpValues(Map<Identifier, HeapThunk> upValues) {
        this.upValues = upValues;
    }

    public HeapObject getSelf() {
        return self;
    }

    public void setSelf(HeapObject self) {
        this.self = self;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<Identifier> getParams() {
        return params;
    }

    public void setParams(List<Identifier> params) {
        this.params = params;
    }

    public AST getBody() {
        return body;
    }

    public void setBody(AST body) {
        this.body = body;
    }

    public long getBuiltin() {
        return builtin;
    }

    public void setBuiltin(long builtin) {
        this.builtin = builtin;
    }
}
