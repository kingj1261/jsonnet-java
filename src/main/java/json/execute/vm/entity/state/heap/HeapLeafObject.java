package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

public class HeapLeafObject extends HeapObject {

    public HeapLeafObject(GarbageCollectionMark mark) {
        super(mark);
    }
}
