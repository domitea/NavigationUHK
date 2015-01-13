package uhk.kikm.navigationuhk.model;

import java.util.ArrayList;

/**
 * Created by dominik on 13.1.15.
 */
public class Level {
    int id;
    int level;
    String description;
    ArrayList<Position> positions;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void addPosition(Position p)
    {
        positions.add(p);

    }
}
