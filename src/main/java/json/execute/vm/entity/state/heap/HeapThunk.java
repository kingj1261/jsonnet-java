package json.execute.vm.entity.state.heap;

import json.execute.entity.ast.AST;
import json.execute.entity.ast.Identifier;
import json.execute.vm.entity.state.GarbageCollectionMark;
import json.execute.vm.entity.state.Value;

import java.util.Map;

public class HeapThunk extends HeapEntity {

    private boolean filled;
    private Value content;
    private Identifier name;
    private Map<Identifier, HeapThunk> upValues;
    private HeapObject self;
    private int offset;
    private AST body;

    public HeapThunk(GarbageCollectionMark mark) {
        super(mark);
    }

    public HeapThunk(GarbageCollectionMark mark, Identifier name, HeapObject self, int offset, AST body) {
        super(mark);
        this.filled = false;
        this.name = name;
        this.self = self;
        this.offset = offset;
        this.body = body;
    }

    public void fill(Value value) {
        content = value;
        filled = true;
        self = null;
        upValues.clear();
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public Value getContent() {
        return content;
    }

    public void setContent(Value content) {
        this.content = content;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
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

    public AST getBody() {
        return body;
    }

    public void setBody(AST body) {
        this.body = body;
    }
}
