package json.execute.vm.entity.state;

import json.execute.vm.entity.state.heap.HeapEntity;

public class UnionHeapEntity {

    private HeapEntity h;
    private double d;
    private boolean b;

    public UnionHeapEntity(HeapEntity h, double d, boolean b) {
        this.h = h;
        this.d = d;
        this.b = b;
    }

    public UnionHeapEntity() {
    }

    public HeapEntity getH() {
        return h;
    }

    public void setH(HeapEntity h) {
        this.h = h;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }
}
