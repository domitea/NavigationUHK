package uhk.kikm.navigationuhk.model;

import java.util.ArrayList;

/**
 * Modelova trida reprezentujici nejakou pozici
 */
public class Position {
    int x;
    int y;
    String description;
    ArrayList<Scan> scans;

    public Position() {
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
}
