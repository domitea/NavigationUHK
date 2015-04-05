package uhk.kikm.navigationuhk.model;

import android.bluetooth.BluetoothDevice;

/**
 * Created by dominik on 4.4.15.
 */
public class BleScan {

    int rssi;
    byte[] scanRecord;
    String address;

    public BleScan(int rssi, byte[] scanRecord, String address) {
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.address = address;
    }
}
