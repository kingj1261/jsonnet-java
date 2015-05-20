package json.execute.vm.entity.state.heap;

import json.execute.entity.ast.Identifier;
import json.execute.vm.entity.state.Field;
import json.execute.vm.entity.state.GarbageCollectionMark;

import java.util.Map;

public class HeapSimpleObject extends HeapLeafObject {

    private Map<Identifier, HeapThunk> upValues;
    private Map<Identifier, Field> fields;

    public HeapSimpleObject(GarbageCollectionMark mark, Map<Identifier, HeapThunk> upValues, Map<Identifier, Field> fields) {
        super(mark);
        this.upValues = upValues;
        this.fields = fields;
    }

    public Map<Identifier, HeapThunk> getUpValues() {
        return upValues;
    }

    public void setUpValues(Map<Identifier, HeapThunk> upValues) {
        this.upValues = upValues;
    }

    public Map<Identifier, Field> getFields() {
        return fields;
    }

    public void setFields(Map<Identifier, Field> fields) {
        this.fields = fields;
    }
}
