package uhk.kikm.navigationuhk.utils;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.Position;
import uhk.kikm.navigationuhk.dataLayer.Scan;

/**
 * Implementace vyhledavani pomoci Bluetooth - je defacto stejna jako i WifiFinderu
 */
public class BluetoothFinder {
    private ArrayList<Position> positions;
    private HashMap<String, Position> navigationData;
    private HashMap<Scan, Position> positionsOfScans;
    private HashMap<Float, Position> computedDistance;

    private final double SIGNAL_NO_RECIEVED = -100; // Minimalni sila signalu, ktery dokaze Bluetooth prijimat - Ekvivalent "nuly" TODO: Overit!

    public BluetoothFinder(ArrayList<Position> positions) {

        navigationData = new HashMap<>();
        positionsOfScans = new HashMap<>();

        this.positions = positions;
        for (Position p : positions) // pro vsechny polohy co obsahuji adresu
        {
            navigationData.put(String.valueOf(p.getX()) + " " + String.valueOf(p.getY()), p); // pridej do seznamu vsechny scany s hasem polohy
            for (Scan s : p.getScans()) {
                // Kazdy sken patri k urcite poloze... Kazdy sken je take jedinecny, hodnoty muzou byt stejne, ale je jedinencny - je to rychlejsi, nez proheledavani cyklem
                positionsOfScans.put(s, p);
            }
        }

        computedDistance = new HashMap<>();
    }

    public Position getPosition(List<ScanResult> scansForIdentify) {

        float distance = 0;

        Position nearestPosition;

        for (Position p : positions)
        {

            if (p.getScans().size() < scansForIdentify.size()) {
                for (ScanResult s : scansForIdentify) {
                    int index = containsMAC(s.BSSID, p);

                    if (index >= 0) {
                        distance += Math.pow(p.getScan(index).getStrenght() + s.level, 2);
                    }
                    else if (index == -1)
                    {
                        distance += Math.pow(SIGNAL_NO_RECIEVED + s.level, 2);
                    }
                }
            } else {
                for (Scan s : p.getScans()) {
                    int index = containsMAC(s.getMAC(), scansForIdentify);

                    if (index >= 0) {
                        distance += Math.pow(scansForIdentify.get(index).level + s.getStrenght(), 2);
                    } else if (index == -1) {
                        distance += Math.pow(SIGNAL_NO_RECIEVED + s.getStrenght(), 2);
                    }
                }
            }


            distance = (float) Math.sqrt(distance);

            computedDistance.put(distance, p); // hashmapa rikajici, ze ta a ta pozice ma takovou a makovou vzdalenost

            distance = 0;
        }

        ArrayList<Float> sortedDistances = new ArrayList<>(computedDistance.keySet()); // Vezmeme pouze vzdalenosti

        Collections.sort(sortedDistances); // setridime

        nearestPosition = computedDistance.get(sortedDistances.get(0)); // a prvni bude nejmensi, takze podle Hash mapy mame i polohu

        return nearestPosition;
    }

    private int containsMAC(String s, Position p)
    {
        for(int i = 0; i < p.getScans().size(); i++)
        {
            if (s.equals(p.getScan(i).getMAC()))
            {
                return i;
            }
        }
        return -1;
    }

    private int containsMAC(String s, List<ScanResult> scans)
    {
        for(int i = 0; i < scans.size(); i++)
        {
            if (s.equals(scans.get(i).BSSID))
            {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }


}
