package uhk.kikm.navigationuhk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.HashMap;

import uhk.kikm.navigationuhk.dataLayer.BleScan;

/**
 * Protoze uzasny Android nema v teto verzi plne implememntovany scanRecord, je pouzito reseni nalezene na internetu
 * http://blog.conjure.co.uk/2014/08/ibeacons-and-android-parsing-the-uuid-major-and-minor-values/
 *
 *
 */
public class BluetoothLEScanCallback implements BluetoothAdapter.LeScanCallback {

    private HashMap<String, BleScan> bleDeviceList;

    public BluetoothLEScanCallback() {
        bleDeviceList = new HashMap<>();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (bleDeviceList.containsKey(device.getAddress())) // pokud uz v seznamu je, aktualizujeme data
        {
            bleDeviceList.remove(device.getAddress());
            bleDeviceList.put(device.getAddress(), new BleScan(rssi, scanRecord, device.getAddress()));
        }
        else
        {
            bleDeviceList.put(device.getAddress(), new BleScan(rssi, scanRecord, device.getAddress()));
        }

    }

    public void clear()
    {
        bleDeviceList.clear();
    }

    public ArrayList<BleScan> getBleScansList()
    {
        ArrayList<BleScan> scans = new ArrayList<>(bleDeviceList.values());
        return scans;
    }
}
