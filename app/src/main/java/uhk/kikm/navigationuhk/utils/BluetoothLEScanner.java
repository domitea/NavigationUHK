package uhk.kikm.navigationuhk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.ArrayList;

/** Trida reprezentujici Bluetooth skenovani
 *
 */
public class BluetoothLEScanner {

    Context context;

    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> bleDeviceList = new ArrayList<>();

    public BluetoothLEScanner(Context context) {
        this.context = context;

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) || !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(context, "BLE not supported", Toast.LENGTH_LONG).show();
        }
        else
        {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public void findAll()
    {
        if (!bluetoothAdapter.isEnabled())
        {
            Toast.makeText(context, "Bluetooth must be up", Toast.LENGTH_LONG).show();
        }
        else
        {
            bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (!bleDeviceList.contains(device))
                    {
                        bleDeviceList.add(device);
                    }
                }
            });
        }
    }

    public ArrayList<BluetoothDevice> getBleDeviceList() {
        return bleDeviceList;
    }
}
