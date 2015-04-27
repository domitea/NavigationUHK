package uhk.kikm.navigationuhk;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uhk.kikm.navigationuhk.dataLayer.BleScan;
import uhk.kikm.navigationuhk.dataLayer.CouchDBManager;
import uhk.kikm.navigationuhk.dataLayer.Fingerprint;
import uhk.kikm.navigationuhk.utils.BluetoothFinder;
import uhk.kikm.navigationuhk.utils.BluetoothLEScanner;
import uhk.kikm.navigationuhk.utils.DeviceInformation;
import uhk.kikm.navigationuhk.utils.LocalizationService.LocalizationService;
import uhk.kikm.navigationuhk.utils.SensorScanner;
import uhk.kikm.navigationuhk.utils.WebViewInterface;
import uhk.kikm.navigationuhk.utils.WifiFinder;
import uhk.kikm.navigationuhk.utils.WifiScanner;

/**
 * Activita určená na tvorbu fingerprintů a příležitostné hlednání
 *
 * Dominik Matoulek 2015
 */

public class CollectorActivity extends ActionBarActivity {

    WebViewInterface webInterface;
    WifiScanner wScanner;
    BluetoothLEScanner bleScanner;
    boolean scanningBle;
    CouchDBManager dbManager;
    WebView view;
    SensorScanner sensorScanner;
    DeviceInformation deviceInformation;
    LocalizationService localizationService;
    int selectedLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webInterface = new WebViewInterface(this);

        view = (WebView) findViewById(R.id.WebView);
        view.getSettings().setJavaScriptEnabled(true); // povoleni JS
        view.getSettings().setBuiltInZoomControls(true); // Zapnuti zoom controls
        view.getSettings().setSupportZoom(true);
        view.setWebViewClient(new WebViewClient());
        view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8"); // nacteni souboru do prohlizece
        view.addJavascriptInterface(webInterface, "android"); // nastaveni JS interface

        final Button newPointButton = (Button) findViewById(R.id.write_point);

        wScanner = new WifiScanner(this); // inicializace WifiScanneru - Skenuje okolni Wifi sítě
        wScanner.findAll(); // zacni skenovat

        newPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePoint();
            }
        });

        sensorScanner = new SensorScanner(this); //inicializace SensorScanneru - Snima polohu zarizeni v prostoru
        deviceInformation = new DeviceInformation(this); // inicializace DeviceInfromation - ziskava informace o telefonu

        scanningBle = false;

        bleScanner = new BluetoothLEScanner(this); // inicializace BLEScanneru - snima okoli pro pripadne BLE beacony
        bleScanner.findAll(); // zacni skenovat

        dbManager = new CouchDBManager(this); // inicializace DB managera
        System.out.println("Open db connection in MainActivity");

        localizationService = new LocalizationService(SettingsFactory.pointA, SettingsFactory.pointB, SettingsFactory.pointC); // nastavujeme souradnicovy system pro vypocet GPS souradnic

        selectedLevel = 2;
        Toast.makeText(this , selectedLevel + ". Patro", Toast.LENGTH_SHORT).show();
    }

    public void writePoint()
    {
        if(webInterface.isChanged()) // pokud se ziskane souradnice u webinterface zmenily, muzeme to zaznamenat
        {
            Toast.makeText(this, webInterface.getX() + " " + webInterface.getY(), Toast.LENGTH_LONG).show();
            webInterface.setChanged(false);

            Fingerprint p = wScanner.createFingerprintLikeFactory(webInterface.getX(), webInterface.getY()); // Tovarnicka na "vyrobu" fingerprintu
            p.setLevel(selectedLevel); // nastavime patro
            sensorScanner.fillPosition(p);  // naplnime daty ze senzoru
            deviceInformation.fillPosition(p); // naplnime infomacemi o zarizeni
            localizationService.getPoint(p); // naplnime vypocitanymi GPS souradnicemi
            p.setBleScans(bleScanner.getBleDeviceList()); // naplnime daty z Bluetooth
            dbManager.savePosition(p); // Ulozime pozici v DB
            bleScanner.clear();
        }
        else
        {
            Toast.makeText(this, "Musi byt jine souradnice", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { // nastaveni
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_about) // Seznam fingerprintů
        {
            Intent intent = new Intent(this, ListPositionsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_find) // Wifi hledani
        {
            findPosition();
        }
        else if(id == R.id.action_level_1) { // 1. patro
            selectedLevel = 1;
            Toast.makeText(this , selectedLevel + ". Patro", Toast.LENGTH_SHORT).show();
            changeLevel(selectedLevel);
        }
        else if(id == R.id.action_level_2) { // 2. patro
            selectedLevel = 2;
            Toast.makeText(this , selectedLevel + ". Patro", Toast.LENGTH_SHORT).show();
            changeLevel(selectedLevel);
        }
        else if(id == R.id.action_level_3) { // 3. patro
            selectedLevel = 3;
            Toast.makeText(this , selectedLevel + ". Patro", Toast.LENGTH_SHORT).show();
            changeLevel(selectedLevel);
        }
        else if(id == R.id.action_level_4) { // 4. patro
            selectedLevel = 4;
            Toast.makeText(this , selectedLevel + ". Patro", Toast.LENGTH_SHORT).show();
            changeLevel(selectedLevel);
        }
        else if(id == R.id.action_find_ble) { // BLE hledani
            findPositionByBle();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reloaduje obrazek patra
     * @param level cislo patra
     */
    private void changeLevel(int level) {
        switch (level) {
            case 1:
                view.loadData(readTextFromResource(R.drawable.uhk_j_1_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;
            case 2:
                view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;
            case 3:
                view.loadData(readTextFromResource(R.drawable.uhk_j_3_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;
            case 4:
                view.loadData(readTextFromResource(R.drawable.uhk_j_4_level), null, "UTF-8"); // nacteni souboru do prohlizece
                view.reload();
                break;

        }
    }

    /**
     * Metoda na nacteni textu z nejakeho souboru.
     * @param resourceID ID zdroje
     * @return String text
     */
    private String readTextFromResource(int resourceID)
    {
        InputStream raw = getResources().openRawResource(resourceID);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int i;
        try
        {
            i = raw.read();
            while (i != -1)
            {
                stream.write(i);
                i = raw.read();
            }
            raw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stream.toString();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        dbManager = new CouchDBManager(this);
        System.out.println("Open db connection in MainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();

        dbManager.closeConnection();
        System.out.println("Close db connection in MainActivity");
        bleScanner.stopScan();
    }

    /**
     * Hledani pozice z ulozenych fingerprintu na zaklade wifi dat
     */
    private void findPosition()
    {
        wScanner.findAll();
        List<ScanResult> scanResults = wScanner.getScanResults(); // Ziskej nalezene Wifi site
        ArrayList<Fingerprint> fingerprints = new ArrayList<>();

        for (ScanResult s : scanResults) // ziskani dat z db
        {
            String[] mac = new String[] {s.BSSID};
            List<Fingerprint> pos = dbManager.getFingerprintsByMacs(mac);
            fingerprints.addAll(pos);
        }

        if (fingerprints.size() > 0) { // pokud je jich vic jak 0, muzeme je predat na prohledani
            WifiFinder finder = new WifiFinder(fingerprints);
            Fingerprint possibleFingerprint = finder.computePossibleFingerprint(scanResults);

            // zobrazime na mape
            view.loadUrl("javascript:setPoint(" + String.valueOf(possibleFingerprint.getX()) + ", " + String.valueOf(possibleFingerprint.getY()) + ", \"blue\"" + ")");
        }
        else
        {
            Toast.makeText(this , "Nedostatek Wifi dat", Toast.LENGTH_SHORT).show();
        }



    }

    /**
     * Hledani pozice z ulozenych fingerprintu na zaklade BLE dat
     */
    private void findPositionByBle()
    {
        List<BleScan> bleScans = bleScanner.getBleDeviceList(); // ziskani nalezenych ble vysilacu

        ArrayList<Fingerprint> bleFingerprints = new ArrayList<>();

        for (BleScan s : bleScans)// ziskani dat z db
        {
            String[] address = new String[] {s.getAddress()};
            List<Fingerprint> pos = dbManager.getPositionsByBleAddresses(address);

            bleFingerprints.addAll(pos);
        }

        if (bleFingerprints.size() > 0) // Pokud jsou nejake fingerprinty nalezene, muzeme prohledavat
        {
            BluetoothFinder bleFinder = new BluetoothFinder(bleFingerprints);
            Fingerprint possibleBleFingerprint = bleFinder.computePossiblePosition(bleScans);

            // zobrazime na mape
            view.loadUrl("javascript:setBlePoint(" + String.valueOf(possibleBleFingerprint.getX()) + ", " + String.valueOf(possibleBleFingerprint.getY()) + ", \"purple\"" + ")");
        }
        else
        {
            Toast.makeText(this , "Nedostatek Bluetooth dat", Toast.LENGTH_SHORT).show();
        }


    }
}
