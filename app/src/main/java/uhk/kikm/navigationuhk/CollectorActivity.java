package uhk.kikm.navigationuhk;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import uhk.kikm.navigationuhk.dataLayer.Position;
import uhk.kikm.navigationuhk.utils.BluetoothLEScanner;
import uhk.kikm.navigationuhk.utils.DeviceInformation;
import uhk.kikm.navigationuhk.utils.LocalizationService.LocalizationService;
import uhk.kikm.navigationuhk.utils.SensorScanner;
import uhk.kikm.navigationuhk.utils.WebViewInterface;
import uhk.kikm.navigationuhk.utils.WifiFinder;
import uhk.kikm.navigationuhk.utils.WifiScanner;


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
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setSupportZoom(true);
        view.setWebViewClient(new WebViewClient());
        view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8");
        view.addJavascriptInterface(webInterface, "android");

        final Button newPointButton = (Button) findViewById(R.id.write_point);
        final Button newBlePointButton = (Button) findViewById(R.id.write_point_ble);

        wScanner = new WifiScanner(this);
        wScanner.findAll();

        newPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePoint();
            }
        });
        newBlePointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBlePoint();
            }
        });

        sensorScanner = new SensorScanner(this);
        deviceInformation = new DeviceInformation(this);

        scanningBle = false;

        bleScanner = new BluetoothLEScanner(this);
        bleScanner.findAll();

        dbManager = new CouchDBManager(this);
        System.out.println("Open db connection in MainActivity");

        localizationService = new LocalizationService(SettingsFactory.pointA, SettingsFactory.pointB, SettingsFactory.pointC); // nastavujeme souradnicovy system pro vypocet GPS souradnic

        selectedLevel = 2;
    }

    public void writeBlePoint()
    {
        if (!scanningBle)
        {
            bleScanner.findAll();
            scanningBle = true;
        }
        else
        {
            bleScanner.stopScan();
            bleScanner.clear();
            scanningBle = false;
        }
    }

    public void writePoint()
    {
        if(webInterface.isChanged())
        {
            Toast.makeText(this, webInterface.getX() + " " + webInterface.getY(), Toast.LENGTH_LONG).show();
            webInterface.setChanged(false);

            Position p = wScanner.getPosition(webInterface.getX(), webInterface.getY());
            p.setLevel(selectedLevel);
            sensorScanner.fillPosition(p);
            deviceInformation.fillPosition(p);
            localizationService.getPoint(p);
            bleScanner.stopScan();
            p.setBleScans(bleScanner.getBleDeviceList());
            dbManager.savePosition(p);
            bleScanner.clear();
            bleScanner.findAll();
        }
        else
        {
            Toast.makeText(this, "Musi byt jine souradnice", Toast.LENGTH_SHORT).show();
        }

        // debug....

        /*List<Position> pos = dbManager.getAllPositions();
        System.out.println(pos.toString());
        dbManager.deleteAll();*/
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_about)
        {
            Intent intent = new Intent(this, ListPositionsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_find)
        {
            findPosition();
        }
        else if(id == R.id.action_level_1)
            selectedLevel = 1;
        else if(id == R.id.action_level_2)
            selectedLevel = 2;
        else if(id == R.id.action_level_3)
            selectedLevel = 3;
        else if(id == R.id.action_level_4)
            selectedLevel = 4;

        return super.onOptionsItemSelected(item);
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

    private void findPosition()
    {
        wScanner.findAll();
        List<ScanResult> scanResults = wScanner.getScanResults();
        /*ScanResult result = scanResults.get(0);

        ArrayList<Position> positions = new ArrayList<>(dbManager.getPostionsByMac(result.BSSID));*/

        String[] macs = new String[scanResults.size()];

        for (int i = 0; i < macs.length; i++)
            macs[i] = scanResults.get(i).BSSID;

        ArrayList<Position> positions = new ArrayList<>(dbManager.getPositionsByMacs(macs));

        WifiFinder finder = new WifiFinder(positions);
        Position possiblePosition = finder.getPosition(scanResults);

        view.loadUrl("javascript:setPoint("+ String.valueOf(possiblePosition.getX()) + ", " + String.valueOf(possiblePosition.getY()) + ")");
    }
}
