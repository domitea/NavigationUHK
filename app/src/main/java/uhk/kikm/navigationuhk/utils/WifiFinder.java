package uhk.kikm.navigationuhk.utils;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uhk.kikm.navigationuhk.model.Position;
import uhk.kikm.navigationuhk.model.Scan;

/**
 * Trida reprezentujici vyhledavani polohy
 */
public class WifiFinder {
    private ArrayList<Position> positions;
    private HashMap<String, ArrayList<Scan>> navigationData;
    private HashMap<Scan, Position> positionsOfScans;

    public WifiFinder(ArrayList<Position> positions) {
        this.positions = positions;
        for (Position p : positions) // pro vsechny polohy patre
        {
            navigationData.put(String.valueOf(p.getX()) + String.valueOf(p.getY()), p.getScans()); // pridej do seznamu vsechny scany s hasem polohy
            for (Scan s : p.getScans()) {
                // Kazdy sken patri k urcite poloze... Kazdy sken je take jedinecny, hodnoty muzou byt stejne, ale je jedinencny - je to rychlejsi, nez proheledavani cyklem
                positionsOfScans.put(s, p);
            }
        }
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public Position getPosition(List<ScanResult> scanForIdentify) {

        for (Position p : positions)
        {
            for (ScanResult s : scanForIdentify)
            {

            }
        }

        return new Position();
    }

    private int containsMAC(Scan s, Position p)
    {
        for(int i = 0; i < p.getScans().size(); i++)
        {
            if (s.getMAC().equals(p.getScan(i).getMAC()))
            {
                return i;
            }
        }
        return -1;
    }

}
