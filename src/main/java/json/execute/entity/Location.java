package json.execute.entity;

public class Location {

    private long line;
    private long column;

    public long getColumn() {
        return column;
    }

    public long getLine() {
        return line;
    }

    public void setColumn(long column) {
        this.column = column;
    }

    public void setLine(long line) {
        this.line = line;
    }
}
