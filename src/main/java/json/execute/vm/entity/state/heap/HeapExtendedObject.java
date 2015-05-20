package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

public class HeapExtendedObject extends HeapObject {

    private HeapObject left;
    private HeapObject right;

    public HeapExtendedObject(GarbageCollectionMark mark, HeapObject left, HeapObject right) {
        super(mark);
        this.left = left;
        this.right = right;
    }

    public HeapObject getLeft() {
        return left;
    }

    public void setLeft(HeapObject left) {
        this.left = left;
    }

    public HeapObject getRight() {
        return right;
    }

    public void setRight(HeapObject right) {
        this.right = right;
    }

}
