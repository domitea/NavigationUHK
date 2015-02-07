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
    private HashMap<String, Position> navigationData;
    private HashMap<Scan, Position> positionsOfScans;
    private HashMap<Float, Position> computedDistance;

    public WifiFinder(ArrayList<Position> positions) {

        this.positions = positions;
        for (Position p : positions) // pro vsechny polohy patre
        {
            navigationData.put(String.valueOf(p.getX()) + " " + String.valueOf(p.getY()), p); // pridej do seznamu vsechny scany s hasem polohy
            for (Scan s : p.getScans()) {
                // Kazdy sken patri k urcite poloze... Kazdy sken je take jedinecny, hodnoty muzou byt stejne, ale je jedinencny - je to rychlejsi, nez proheledavani cyklem
                positionsOfScans.put(s, p);
            }
        }

        computedDistance = new HashMap<>();
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public Position getPosition(List<ScanResult> scansForIdentify) {

        float distance = 0;

        Position nearestPosition;

        for (Position p : positions)
        {
            for (ScanResult s : scansForIdentify)
            {
                int index = containsMAC(s,p);

                if (index >= 0)
                {
                    distance += Math.pow(s.level - p.getScan(index).getStrenght(), 2);
                }
                else if (index == -1)
                {
                    distance += Math.pow(0 - p.getScan(index).getStrenght(), 2);
                }
            }

            distance = (float) Math.sqrt(distance);

            computedDistance.put(distance,p);


        }

        return nearestPosition;
    }

    private int containsMAC(ScanResult s, Position p)
    {
        for(int i = 0; i < p.getScans().size(); i++)
        {
            if (s.BSSID.equals(p.getScan(i).getMAC()))
            {
                return i;
            }
        }
        return -1;
    }

}
