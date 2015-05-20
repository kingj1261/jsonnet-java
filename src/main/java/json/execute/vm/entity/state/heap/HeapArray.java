package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;

import java.util.List;

public class HeapArray extends HeapEntity {

    private List<HeapThunk> elements;

    public HeapArray(GarbageCollectionMark mark, List<HeapThunk> elements) {
        super(mark);
        this.elements = elements;
    }

    public List<HeapThunk> getElements() {
        return elements;
    }

    public void setElements(List<HeapThunk> elements) {
        this.elements = elements;
    }
}
