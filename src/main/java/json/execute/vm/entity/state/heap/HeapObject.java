package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

public class HeapObject extends HeapEntity {

    public HeapObject(GarbageCollectionMark mark) {
        super(mark);
    }

}
