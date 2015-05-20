package json.execute.vm.entity.state.heap;

import json.execute.entity.ast.AST;
import json.execute.entity.ast.Identifier;
import json.execute.vm.entity.state.GarbageCollectionMark;

import java.util.Map;

public class HeapComprehensionObject extends HeapLeafObject {

    private Map<Identifier, HeapThunk> upValues;
    private AST value;
    private Identifier id;
    private Map<Identifier, HeapThunk> compValues;

    public HeapComprehensionObject(GarbageCollectionMark mark,
                                   Map<Identifier, HeapThunk> upValues,
                                   AST value, Identifier id,
                                   Map<Identifier, HeapThunk> compValues) {
        super(mark);
        this.upValues = upValues;
        this.value = value;
        this.id = id;
        this.compValues = compValues;
    }

    public Map<Identifier, HeapThunk> getUpValues() {
        return upValues;
    }

    public void setUpValues(Map<Identifier, HeapThunk> upValues) {
        this.upValues = upValues;
    }

    public AST getValue() {
        return value;
    }

    public void setValue(AST value) {
        this.value = value;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Map<Identifier, HeapThunk> getCompValues() {
        return compValues;
    }

    public void setCompValues(Map<Identifier, HeapThunk> compValues) {
        this.compValues = compValues;
    }
}
