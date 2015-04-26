package uhk.kikm.navigationuhk.dataLayer;

import java.util.Arrays;

/**
 * Datova trida obsahujici informace o zaznamenanem BLE zarizeni
 * Dominik Matoulek 2015
 */
public class BleScan {

    int rssi;
    byte[] scanRecord;
    String address;

    public BleScan() {
    }

    public BleScan(int rssi, byte[] scanRecord, String address) {
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.address = address;
    }

    @Override
    public String toString() {
        return "BleScan{" +
                "rssi=" + rssi +
                ", scanRecord=" + Arrays.toString(scanRecord) +
                ", address='" + address + '\'' +
                '}';
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
