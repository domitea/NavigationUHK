package uhk.kikm.navigationuhk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.dataLayer.Scan;

/** Trida reprezentujici skenovani Wifi
 *  Dominik Matoulek
 */
public class WifiScanner {

    Context context;

    List<ScanResult> scanResults;

    WifiManager wm;

    /**
     * Inicializuje WifiScanner
     * @param context context
     */
    public WifiScanner(Context context) {
        this.context = context;
    }

    /**
     * Nalezne vsechny wifi site v okoli telefonu
     */
    public void findAll()
    {
        wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        if (!wm.isWifiEnabled())
            wm.setWifiEnabled(true);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println(wm.getScanResults().toString());
                scanResults = wm.getScanResults();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wm.startScan();

    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    /**
     * Tovarnicka na vyrobu fingerprintu
     * @param x souradnice x ze zobrazene mapy
     * @param y souradnice y ze zobrazene mapy
     * @return vytvoreny prazdny fingertprint
     */
    public Fingerprint createFingerprintLikeFactory(int x, int y)
    {
        Fingerprint p = new Fingerprint();
        p.setX(x);
        p.setY(y);

        for (ScanResult sr : scanResults)
        {
            p.addScan(new Scan(sr.SSID, sr.BSSID, sr.level));
        }
        return p;
    }
}
