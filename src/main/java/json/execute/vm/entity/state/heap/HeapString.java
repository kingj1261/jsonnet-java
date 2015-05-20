package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

public class HeapString extends HeapEntity {

    private String value;

    public HeapString(GarbageCollectionMark mark, String value) {
        super(mark);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
