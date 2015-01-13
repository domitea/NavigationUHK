package uhk.kikm.navigationuhk.model;

import java.util.ArrayList;

/**
 * Modelova trida reprezentujici patra budovy
 *
 */
public class Level {
    int level;
    String description;
    ArrayList<Position> positions;

    public Level(int level) {
        this.level = level;
    }

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

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void addPosition(Position p)
    {
        positions.add(p);

    }
}
