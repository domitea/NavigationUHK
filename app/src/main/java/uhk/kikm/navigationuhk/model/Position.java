package uhk.kikm.navigationuhk.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Modelova trida reprezentujici nejakou pozici
 */
public class Position {

    // couchDB identificator
    String id;

    int level;

    int x;
    int y;
    String description;
    ArrayList<Scan> scans;

    Date createdDate;

    public Position() {
        scans = new ArrayList<>();
        description = "";
    }

    public void addScan(Scan s)
    {
        scans.add(s);
    }

    public ArrayList<Scan> getScans() {
        return scans;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
