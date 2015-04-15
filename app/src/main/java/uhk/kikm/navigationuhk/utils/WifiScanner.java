package uhk.kikm.navigationuhk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.Position;
import uhk.kikm.navigationuhk.dataLayer.Scan;

/** Trida reprezentujici skanovani Wifi
 *
 */
public class WifiScanner {

    Context context;

    List<ScanResult> scanResults;

    WifiManager wm;

    public WifiScanner(Context context) {
        this.context = context;
    }

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

    public Position getPosition(int x, int y)
    {
        Position p = new Position();
        p.setX(x);
        p.setY(y);

        for (ScanResult sr : scanResults)
        {
            p.addScan(new Scan(sr.SSID, sr.BSSID, sr.level));
        }
        return p;
    }
}
