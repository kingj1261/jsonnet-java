package json.execute.vm.entity.state;


public enum Type {
    NULL_TYPE(0x0),  // Unfortunately NULL is a macro in C.
    BOOLEAN(0x1),
    DOUBLE(0x2),

    ARRAY(0x10),
    FUNCTION(0x11),
    OBJECT(0x12),
    STRING(0x13);

    private int id;

    Type(int i) {
        this.id = i;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
