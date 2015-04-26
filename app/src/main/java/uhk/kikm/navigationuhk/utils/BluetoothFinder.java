package uhk.kikm.navigationuhk.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.BleScan;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.dataLayer.Scan;

/**
 * Implementace vyhledavani pomoci Bluetooth - je defacto stejna jako i WifiFinderu
 */
public class BluetoothFinder {
    private ArrayList<Fingerprint> fingerprints;
    private HashMap<String, Fingerprint> navigationData;
    private HashMap<Scan, Fingerprint> positionsOfScans;
    private HashMap<Float, Fingerprint> computedDistance;

    private final double SIGNAL_NO_RECIEVED = -100; // Minimalni sila signalu, ktery dokaze Bluetooth prijimat - Ekvivalent "nuly"

    public BluetoothFinder(ArrayList<Fingerprint> fingerprints) {

        navigationData = new HashMap<>();
        positionsOfScans = new HashMap<>();

        this.fingerprints = fingerprints;
        for (Fingerprint p : fingerprints) // pro vsechny polohy co obsahuji adresu
        {
            navigationData.put(String.valueOf(p.getX()) + " " + String.valueOf(p.getY()), p); // pridej do seznamu vsechny scany s hasem polohy
            for (Scan s : p.getScans()) {
                // Kazdy sken patri k urcite poloze... Kazdy sken je take jedinecny, hodnoty muzou byt stejne, ale je jedinencny - je to rychlejsi, nez proheledavani cyklem
                positionsOfScans.put(s, p);
            }
        }

        computedDistance = new HashMap<>();
    }

    public Fingerprint getPosition(List<BleScan> scansForIdentify) {

        float distance = 0;

        Fingerprint nearestFingerprint;

        for (Fingerprint p : fingerprints)
        {

            if (p.getScans().size() < scansForIdentify.size()) {
                for (BleScan s : scansForIdentify) {
                    int index = containsAddress(s.getAddress(), p);

                    if (index >= 0) {
                        distance += Math.pow(p.getScan(index).getStrenght() + s.getRssi(), 2);
                    }
                    else if (index == -1)
                    {
                        distance += Math.pow(SIGNAL_NO_RECIEVED + s.getRssi(), 2);
                    }
                }
            } else {
                for (BleScan s : p.getBleScans()) {
                    int index = containsAddress(s.getAddress(), scansForIdentify);

                    if (index >= 0) {
                        distance += Math.pow(scansForIdentify.get(index).getRssi() + s.getRssi(), 2);
                    } else if (index == -1) {
                        distance += Math.pow(SIGNAL_NO_RECIEVED + s.getRssi(), 2);
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

    private int containsAddress(String s, Fingerprint p)
    {
        for(int i = 0; i < p.getScans().size(); i++)
        {
            if (s.equals(p.getBleScans().get(i).getAddress()));
            {
                return i;
            }
        }
        return -1;
    }

    private int containsAddress(String s, List<BleScan> scans)
    {
        for(int i = 0; i < scans.size(); i++)
        {
            if (s.equals(scans.get(i).getAddress()))
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
