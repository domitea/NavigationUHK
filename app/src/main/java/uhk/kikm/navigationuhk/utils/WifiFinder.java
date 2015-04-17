package uhk.kikm.navigationuhk.utils;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.dataLayer.Scan;

/**
 * Trida reprezentujici vyhledavani polohy
 */
public class WifiFinder {
    private ArrayList<Fingerprint> fingerprints;
    private HashMap<String, Fingerprint> navigationData;
    private HashMap<Scan, Fingerprint> positionsOfScans;
    private HashMap<Float, Fingerprint> computedDistance;

    private final double SIGNAL_NO_RECIEVED = -100; // Minimalni sila signalu, ktery dokaze WiFi prijimat - Ekvivalent "nuly"

    public WifiFinder(ArrayList<Fingerprint> fingerprints) {

        navigationData = new HashMap<>();
        positionsOfScans = new HashMap<>();

        this.fingerprints = fingerprints;
        for (Fingerprint p : fingerprints) // pro vsechny polohy co obsahuji MAC
        {
            navigationData.put(String.valueOf(p.getX()) + " " + String.valueOf(p.getY()), p); // pridej do seznamu vsechny scany s hasem polohy
            for (Scan s : p.getScans()) {
                // Kazdy sken patri k urcite poloze... Kazdy sken je take jedinecny, hodnoty muzou byt stejne, ale je jedinencny - je to rychlejsi, nez proheledavani cyklem
                positionsOfScans.put(s, p);
            }
        }

        computedDistance = new HashMap<>();
    }

    public Fingerprint getPosition(List<ScanResult> scansForIdentify) {

        float distance = 0;

        Fingerprint nearestFingerprint;

        for (Fingerprint p : fingerprints)
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

        nearestFingerprint = computedDistance.get(sortedDistances.get(0)); // a prvni bude nejmensi, takze podle Hash mapy mame i polohu

        return nearestFingerprint;
    }

    private int containsMAC(String s, Fingerprint p)
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

    public ArrayList<Fingerprint> getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(ArrayList<Fingerprint> fingerprints) {
        this.fingerprints = fingerprints;
    }


}