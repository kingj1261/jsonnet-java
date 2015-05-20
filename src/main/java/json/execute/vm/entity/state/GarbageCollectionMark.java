package json.execute.vm.entity.state;

public class GarbageCollectionMark {

    private char mark;

    public GarbageCollectionMark() {
    }

    public GarbageCollectionMark(char mark) {
        this.mark = mark;
    }

    public char getMark() {
        return mark;
    }

    public void setMark(char mark) {
        this.mark = mark;
    }
}
