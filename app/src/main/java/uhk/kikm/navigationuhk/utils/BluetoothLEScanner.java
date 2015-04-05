package uhk.kikm.navigationuhk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

import uhk.kikm.navigationuhk.model.BleScan;
import uhk.kikm.navigationuhk.model.Position;

/** Trida reprezentujici Bluetooth skenovani
 *
 */
public class BluetoothLEScanner {

    Context context;

    private final long INTERVAL = 1000; //ms

    BluetoothAdapter bluetoothAdapter;

    ArrayList<BleScan> bleDeviceList;

    BluetoothLEScanCallback bluetoothLEScanCallback = new BluetoothLEScanCallback();

    private Handler handler = new Handler();

    private boolean runScan = false;

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
            runScan = true;
            startBleScan.run();
        }
    }

    public void stopScan()
    {
        runScan = false;
    }

    private Runnable startBleScan = new Runnable() {
        @Override
        public void run() {
            if(runScan) {
                bluetoothAdapter.startLeScan(bluetoothLEScanCallback);
                handler.postDelayed(stopBleScan, INTERVAL);
            }
        }
    };

    private Runnable stopBleScan = new Runnable() {
        @Override
        public void run() {
            bluetoothAdapter.stopLeScan(bluetoothLEScanCallback);
            bleDeviceList = bluetoothLEScanCallback.getBleScansList();
            bluetoothLEScanCallback.clear();
            System.out.println(bleDeviceList.toString());
            handler.postDelayed(startBleScan, INTERVAL);
        }
    };

    public void clear()
    {
        bleDeviceList.clear();
    }

    public ArrayList<BleScan> getBleDeviceList() {
        return bleDeviceList;
    }
}
