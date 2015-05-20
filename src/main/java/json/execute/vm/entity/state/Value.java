package json.execute.vm.entity.state;

public class Value {

    private Type t;
    private UnionHeapEntity v;

    public Value(Type t, UnionHeapEntity v) {
        this.t = t;
        this.v = v;
    }

    public boolean isHeap() {
        return t.getId() == 0x10;
    }

    public Type getT() {
        return t;
    }

    public void setT(Type t) {
        this.t = t;
    }

    public UnionHeapEntity getV() {
        return v;
    }

    public void setV(UnionHeapEntity v) {
        this.v = v;
    }
}
