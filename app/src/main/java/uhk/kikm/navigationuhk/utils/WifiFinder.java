package uhk.kikm.navigationuhk.utils;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;

import uhk.kikm.navigationuhk.model.Level;
import uhk.kikm.navigationuhk.model.Position;
import uhk.kikm.navigationuhk.model.Scan;

/**
 * Trida reprezentujici vyhledavani polohy
 */
public class WifiFinder {
    private Level level;
    private HashMap<String, ArrayList<Scan>> navigationData;

    public WifiFinder(Level level) {
        this.level = level;
        for (Position p : level.getPositions()) // pro vsechny polohy patre
        {
            navigationData.put(String.valueOf(p.getX()) + String.valueOf(p.getY()), p.getScans()); // pridej do seznamu vsechny scany s hasem polohy
        }
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Position getPosition(ScanResult scanForIdentify)
    {
       return new Position();
    }

    private Position getPosition(Scan s)
    {
        for (Position p : level.getPositions())
        {
            if (p.getScans().contains(s))
            {
                return p;
            }
        }
        return null;
    }
}
