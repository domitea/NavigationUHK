package uhk.kikm.navigationuhk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
                scanResults = wm.getScanResults();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wm.startScan();
    }
}
