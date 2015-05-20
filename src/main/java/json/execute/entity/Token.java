package json.execute.entity;

public class Token {

    private Kind kind;
    private String data;
    private LocationRange location;

    public Token(Kind kind, String data) {
        this.kind = kind;
        this.data = data;
    }

    public Kind getKind() {
        return kind;
    }

    public LocationRange getLocation() {
        return location;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setLocation(LocationRange location) {
        this.location = location;
    }

}
