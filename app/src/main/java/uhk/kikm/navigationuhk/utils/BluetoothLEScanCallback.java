package uhk.kikm.navigationuhk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.HashMap;

import uhk.kikm.navigationuhk.dataLayer.BleScan;

/**
 * Callback pouzity u BluetoothLEScanner
 * Dominik Matoulek 2015
 */
public class BluetoothLEScanCallback implements BluetoothAdapter.LeScanCallback {

    private HashMap<String, BleScan> bleDeviceList;

    public BluetoothLEScanCallback() {
        bleDeviceList = new HashMap<>();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (bleDeviceList.containsKey(device.getAddress())) // pokud zarizeni uz v seznamu je, aktualizujeme data
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
