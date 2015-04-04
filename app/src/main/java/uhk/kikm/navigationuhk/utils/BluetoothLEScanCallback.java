package uhk.kikm.navigationuhk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by dominik on 4.4.15.
 */
public class BluetoothLEScanCallback implements BluetoothAdapter.LeScanCallback {

    private ArrayList<BluetoothDevice> bleDeviceList;

    public BluetoothLEScanCallback() {
        bleDeviceList = new ArrayList<>();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!bleDeviceList.contains(device))
        {
            bleDeviceList.add(device);
        }
        System.out.println(bleDeviceList.toString());
    }
}
