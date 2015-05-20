package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

public class HeapEntity {

    private GarbageCollectionMark mark;

    public HeapEntity(GarbageCollectionMark mark) {
        this.mark = mark;
    }

    public GarbageCollectionMark getMark() {
        return mark;
    }

    public void setMark(GarbageCollectionMark mark) {
        this.mark = mark;
    }
}
