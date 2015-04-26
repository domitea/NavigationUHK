package uhk.kikm.navigationuhk.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

import uhk.kikm.navigationuhk.dataLayer.BleScan;

/** Trida reprezentujici Bluetooth skenovani
 * Dominik Matoulek 2015
 */
public class BluetoothLEScanner {

    Context context;

    private final long INTERVAL = 1000; //ms

    BluetoothAdapter bluetoothAdapter;

    ArrayList<BleScan> bleDeviceList;

    BluetoothLEScanCallback bluetoothLEScanCallback = new BluetoothLEScanCallback();

    private Handler handler = new Handler();

    private boolean runScan = false;

    /**
     * Inicializuje BluetoothLEScanner
     * @param context context
     */
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

    /**
     * Nalezne vsechny zarzeni v okoli telefonu vysilajici na BLE
     */
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

    /**
     * Ukonci skenovani
     */
    public void stopScan()
    {
        runScan = false;
    }

    /**
     * Kvuli ruznorode implemetaci netody onLeScan je nutne zajistit akualni data
     *
     * Proto tu jsou tyto dva Runnble abjekty. Cyklicky zapinaji a vypinaji skenovani, aby byly vzdy dostupne nejaktualnejsi infomace
     */
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
