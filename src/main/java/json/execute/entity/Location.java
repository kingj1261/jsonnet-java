package json.execute.entity;

public class Location {

    private long line;
    private long column;

    public Location(long line_number, long l) {
        this.line = line_number;
        this.column = l;
    }

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
