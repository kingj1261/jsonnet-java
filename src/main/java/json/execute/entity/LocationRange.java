package json.execute.entity;

public class LocationRange {

    private String file;
    private Location begin;
    private Location end;

    public Location getBegin() {
        return begin;
    }

    public Location getEnd() {
        return end;
    }

    public String getFile() {
        return file;
    }

    public void setBegin(Location begin) {
        this.begin = begin;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
