package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

public class HeapSuperObject extends HeapObject {

    private HeapObject root;
    private int offset;

    public HeapSuperObject(GarbageCollectionMark mark, HeapObject root, int offset) {
        super(mark);
        this.root = root;
        this.offset = offset;
    }

    public HeapObject getRoot() {
        return root;
    }

    public void setRoot(HeapObject root) {
        this.root = root;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
