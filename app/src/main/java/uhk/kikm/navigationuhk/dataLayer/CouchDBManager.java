package uhk.kikm.navigationuhk.dataLayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uhk.kikm.navigationuhk.SettingsFactory;

/**
 * Trida reprezentujici komunikaci s DB vyuzivajici model (tridy Fingerprint, Scan a BleScan)
 *
 * Dominik Matoulek 2015
 */
public class CouchDBManager {
    Context context;
    Manager manager;
    Database db;
    URL serverURL;

    final String DB_NAME = "scan_uhk"; // Nazev DB
    final String VIEW_BY_MAC = "by_mac"; // Nazev pohledu, ktery vyhleda dokumetny podle MAC adres
    final String VIEW_BY_BLE_ADDRESS = "by_ble_mac"; // Nazev pohledu, ktery vyhleda dokumenty podle Bluetooth adres
    final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; // Format pouzivaneho casu - ROK-MESIC-DEN HODINA:MINUTA:SEKUNDA


    /**
     * Vytvari instanci DbManageru pro komunikaci s DB Couchbase mibole
     * @param context Context
     */
    public CouchDBManager(Context context) {
        this.context = context;
        try {

            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS); // pripojeni k DB
            this.db = manager.getDatabase(DB_NAME); // Vybrani/vytvoreni DB

            /**
             * "Deklarace" mapovaci fce.
             *
             * Vytvor seznam klic:hodnota, kde klic je MAC a hodnota je poloha, kde byla zaznamenana ->
             * poloha tam bude n-krat, kde n je pocet zaznamenanych MAC.
             *
             * Vypada to jako redudance, ale pres Query se tak daji vytahnout jen zaznamy prislusici jedne MAC.
             */
            db.getView(VIEW_BY_MAC).setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                        List<Map<String, Object>> scans = (List) document.get("scans");
                        for (Map<String, Object> scan : scans) {
                            emitter.emit(scan.get("mac"), document);
                        }

                }
            }, "1");

            // to same, akorat pro Bluetooth Low Energy

            db.getView(VIEW_BY_BLE_ADDRESS).setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                        List<Map<String, Object>> scans = (List) document.get("bleScans");
                        for (Map<String, Object> scan : scans) {
                            emitter.emit(scan.get("address"), document);
                        }

                }
            }, "1");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }

        // parsujeme URL adresu
        try {
            serverURL = new URL(SettingsFactory.DB_URL);
        }catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        }
    }

    /**
     * Ulozeni nekolika fingerprintu
     * @param fingerprints Seznam fingerprintu
     */
    public void savePositions(List<Fingerprint> fingerprints)
    {
        for (Fingerprint p : fingerprints){
            Map<String, Object> properties = getMapOfDocument(p);

            Document doc = db.createDocument();
            try {
                doc.putProperties(properties);
            }
            catch (CouchbaseLiteException cle)
            {
                cle.printStackTrace();
            }

        }
    }

    /**
     * Ulozi fingerprint
     * @param p fingerprint
     */
    public void savePosition(Fingerprint p)
    {
        Map<String, Object> properties = getMapOfDocument(p);

            Document doc = db.createDocument();
            try {
                doc.putProperties(properties);
            }
            catch (CouchbaseLiteException cle)
            {
                cle.printStackTrace();
            }


    }

    /**
     * Vymaze celou DB
     */
    public void deleteAll()
    {
        try
        {
            db.delete();
            this.db = manager.getDatabase(DB_NAME); // Vybrani/vytvoreni DB

            /**
             * "Deklarace" mapovaci fce. Popsana na radku 45
             *
             */
            db.getView(VIEW_BY_MAC).setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    List<Map<String, Object>> scans = (List) document.get("scans");
                    for (Map<String, Object> scan : scans)
                    {
                        emitter.emit(scan.get("mac"), document);
                    }
                }
            }, "1");

            db.getView(VIEW_BY_BLE_ADDRESS).setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    List<Map<String, Object>> scans = (List) document.get("bleScans");
                    for (Map<String, Object> scan : scans) {
                        emitter.emit(scan.get("address"), document);
                    }

                }
            }, "1");
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
    }

    /**
     * Ziska vsehny fingerprinty, ktere maji danou MAC adresu zaznamenanou
     * @param mac Vyberova MAC adresa
     * @return Seznam vsech fingerprintu, kter mac obsahuji
     */
    public List<Fingerprint> getFingerprintsByMac(String mac)
    {
        ArrayList<Fingerprint> fingerprints = new ArrayList<>();
        Query query = db.getView(VIEW_BY_MAC).createQuery();

        query.setStartKey(mac);
        query.setEndKey(mac);

        try
        {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                Document doc = row.getDocument();

                fingerprints.add(getFingerprintFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return fingerprints;
    }

    /**
     * Ziska vsechny fingerprinty, ktere obsahuji dane MAC
     * @param macs pole MAC adres
     * @return Sezanm fingerprintu, ktere obsahuji MAC
     */
    public List<Fingerprint> getFingerprintsByMacs(String[] macs)
    {
        ArrayList<Fingerprint> fingerprints = new ArrayList<>();
        Query query = db.getView(VIEW_BY_MAC).createQuery();

        List<Object> objects = new ArrayList<>();

        for (int i = 0; i < macs.length; i++)
            objects.add( (Object) macs[i]);

        query.setKeys(objects);

        try
        {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                Document doc = row.getDocument();

                fingerprints.add(getFingerprintFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return fingerprints;
    }

    /**
     * Ziska vsechny fingerprinty, ktere obsahuji adresy BLE zarizeni (BLE Address)
     * @param adresses Pole adres
     * @return Seznam vsech fingerprintu, ktere obsahuji dane adresy
     */
    public List<Fingerprint> getPositionsByBleAddresses(String[] adresses)
    {
        ArrayList<Fingerprint> fingerprints = new ArrayList<>();

        Query query = db.getView(VIEW_BY_BLE_ADDRESS).createQuery();

        List<Object> objects = new ArrayList<>();

        for (int i = 0; i < adresses.length; i++)
            objects.add( (Object) adresses[i]);

        query.setKeys(objects);

        try
        {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                Document doc = row.getDocument();

                fingerprints.add(getFingerprintFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }

        return fingerprints;
    }

    /**
     * Vytahne vsechny fingerprinty
     * @return seznam fingerprintu
     */
    public List<Fingerprint> getAllFingerprints()
    {
        Query query = db.createAllDocumentsQuery();
        ArrayList<Fingerprint> fingerprints = new ArrayList<>();

        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                Document doc = row.getDocument();

                fingerprints.add(getFingerprintFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return fingerprints;

    }

    /**
     * Odstrani fingerprint z DB
      * @param id ID fingerprintu
     */
    public void removeFingerprint(String id)
    {
        Document doc = (Document) db.getDocument(id);
        try
        {
            doc.delete();
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
    }

    /**
     * Ziska aktualni cas dle kosntanty DATE_FORMAT
     * @return aktualni cas ve Stringu
     */
    private String getCurrentTime()
    {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date today = GregorianCalendar.getInstance().getTime();

        return df.format(today);
    }

    /**
     * Prevede Stringovy format dle DATE_FORMAT na objekt tridy Date
     * @param date cas ve Stringu
     * @return cas v objektu tridy Date
     */
    private Date getDate(String date)
    {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date created = df.parse(date);
            return created;
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }
        return new Date();
    }

    /**
     * Vytvari mapu dokumetu pro ulozeni do DB
     * @param p Fingerprint, ktery chceme ulozit
     * @return Mapu objektu
     */
    private Map<String, Object> getMapOfDocument(Fingerprint p) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", "scan");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        properties.put("couchbase_sync_gateway_id", sp.getString("couchbase_sync_gateway_id", "no_id"));

        properties.put("x", String.valueOf(p.getX()));
        properties.put("y", String.valueOf(p.getY()));
        properties.put("level", String.valueOf(p.getLevel()));
        properties.put("description", p.getDescription());
        properties.put("createdAt", getCurrentTime());

        properties.put("deviceId", p.getDeviceID());

        // ukladame dalsi data
        properties.put("board", p.getBoard());
        properties.put("bootloader", p.getBootloader());
        properties.put("brand", p.getBrand());
        properties.put("device", p.getDevice());
        properties.put("display", p.getDisplay());
        properties.put("fingerprint", p.getFingerprint());
        properties.put("hardware", p.getHardware());
        properties.put("host", p.getHost());
        properties.put("osId", p.getOsId());
        properties.put("manufacturer", p.getManufacturer());
        properties.put("model", p.getModel());
        properties.put("product", p.getProduct());
        properties.put("serial", p.getSerial());
        properties.put("tags", p.getTags());
        properties.put("type", p.getType());
        properties.put("userAndroid", p.getUser());

        // poloha zarizeni v prostoru...
        properties.put("accX", p.getAccX());
        properties.put("accY", p.getAccY());
        properties.put("accZ", p.getAccZ());

        properties.put("gyroX", p.getGyroX());
        properties.put("gyroY", p.getGyroY());
        properties.put("gyroZ", p.getGyroZ());

        properties.put("magX", p.getMagX());
        properties.put("magY", p.getMagY());
        properties.put("magZ", p.getMagZ());

        // Vypocitane GPS souradnice
        properties.put("lat", p.getLat());
        properties.put("lon", p.getLon());

        // pridani skenu...
        List<Map<String, Object>> scansArray = new ArrayList<>();
        ArrayList<Scan> scans = p.getScans();
        for (Scan s : scans)
        {
            Map<String, Object> scanProperties = new HashMap<>();
            scanProperties.put("ssid",s.getSSID());
            scanProperties.put("mac",s.getMAC());
            scanProperties.put("strentgh",String.valueOf(s.getStrenght()));

            scansArray.add(scanProperties);
        }

        properties.put("scans",scansArray);

        // pridani bluetooth scanu
        List<Map<String, Object>> bleScansArray = new ArrayList<>();
        ArrayList<BleScan> bleScans = p.getBleScans();
        if (bleScans != null) {
            for (BleScan s : bleScans) {
                Map<String, Object> bleScanProperties = new HashMap<>();
                bleScanProperties.put("address", s.getAddress());
                bleScanProperties.put("rssi", String.valueOf(s.getRssi()));

                bleScanProperties.put("scanRecord", s.getScanRecord());

                bleScansArray.add(bleScanProperties);
            }
            properties.put("bleScans",bleScansArray);
        }
        else
        {
            properties.put("bleScans","[]");
        }




        return properties;
    }

    /**
     * Vytvori fingerprint z DB dokumentu
     * @param doc Mapa dokumentu
     * @return Instanci tridy Fingerprint naplnenou daty
     */
    private Fingerprint getFingerprintFormDocument(Document doc)
    {
        Fingerprint p = new Fingerprint();
        // poloha
        p.setX(Integer.parseInt(doc.getProperty("x").toString()));
        p.setY(Integer.parseInt(doc.getProperty("y").toString()));
        // datum vytvoreni zaznamu, resp. datum skenovani
        p.setCreatedDate(getDate(doc.getProperty("createdAt").toString()));
        // nejaky balast
        p.setDescription(parseProperty("description", doc));
        p.setId(doc.getProperty("_id").toString());
        // jake patro....
        p.setLevel(Integer.parseInt(doc.getProperty("level").toString()));

        // parsovani dalsich dat....
        p.setBoard(parseProperty("board", doc));
        p.setBootloader(parseProperty("bootloader", doc));
        p.setBrand(parseProperty("brand", doc));
        p.setDevice(parseProperty("device", doc));
        p.setDisplay(parseProperty("display", doc));
        p.setFingerprint(parseProperty("fingerprint", doc));
        p.setHardware(parseProperty("hardware", doc));
        p.setHost(parseProperty("host", doc));
        p.setOsId(parseProperty("osId", doc));
        p.setManufacturer(parseProperty("manufacturer", doc));
        p.setModel(parseProperty("model", doc));
        p.setProduct(parseProperty("product", doc));
        p.setSerial(parseProperty("serial", doc));
        p.setTags(parseProperty("tags", doc));
        p.setType(parseProperty("type", doc));
        p.setUser(parseProperty("userAndroid", doc));

        p.setDeviceID(parseProperty("deviceId", doc));

        // Parsovani polohy zarizeni v prostoru
        p.setAccX(Float.valueOf(parseProperty("accX", doc)));
        p.setAccY(Float.valueOf(parseProperty("accY", doc)));
        p.setAccZ(Float.valueOf(parseProperty("accZ", doc)));

        p.setGyroX(Float.valueOf(parseProperty("gyroX", doc)));
        p.setGyroY(Float.valueOf(parseProperty("gyroY", doc)));
        p.setGyroZ(Float.valueOf(parseProperty("gyroZ", doc)));

        p.setMagX(Float.valueOf(parseProperty("magX", doc)));
        p.setMagY(Float.valueOf(parseProperty("magY", doc)));
        p.setMagZ(Float.valueOf(parseProperty("magZ", doc)));

        // GPS souradnice
        p.setLat(Float.valueOf(parseProperty("lat", doc)));
        p.setLon(Float.valueOf(parseProperty("lon", doc)));

        // Parsovani skenu... We need to go deeper... List<Map<String, Object>>

        List<Map<String, Object>> scans = (List) doc.getProperty("scans");
        for (Map<String, Object> scan : scans)
        {
            Scan s = new Scan();

            s.setMAC(scan.get("mac").toString());
            s.setSSID(scan.get("ssid").toString());
            s.setStrentgh(Integer.parseInt(scan.get("strentgh").toString()));

            p.addScan(s);
        }

        // parsovanui bleScanu
        if (doc.getProperty("bleScans") != null) { // Pokud neni null
            if (!doc.getProperty("bleScans").equals("[]")) { // nebo nebyly zaznamenany vysilace
                List<Map<String, Object>> bleScans = (List) doc.getProperty("bleScans");
                for (Map<String, Object> scan : bleScans) {
                    BleScan bleScan = new BleScan();

                    bleScan.setAddress(scan.get("address").toString());
                    bleScan.setRssi(Integer.parseInt(scan.get("rssi").toString()));

                    List<Byte> scanRecordList = (List) doc.getProperty("scanRecord") == null ? new ArrayList<>() : (List) doc.getProperty("scanRecord");
                    byte[] scanRecord = new byte[scanRecordList.size()];

                    for (int i = 0; i < scanRecordList.size(); i++) {
                        scanRecord[i] = Byte.valueOf(scanRecordList.get(i));
                    }
                    bleScan.setScanRecord(scanRecord);

                    p.addBleScan(bleScan);
                }
            }
        }

        return p;
    }

    /**
     * Couchabse ma trochu jine chapani nuly/null.
     * @param property Vlastnost na parsovani
     * @param doc Dokument
     * @return Hodnotu vlastnosti ve Stringu
     */
    private String parseProperty(String property, Document doc)
    {
        Object o = doc.getProperty(property); // V chapani JSON muze byt null !!
        if (o == null)
        {
            return "0";
        }
        return o.toString();
    }

    /**
     * Uzavre spojeni
     */
    public void closeConnection()
    {
        db.close();
        manager.close();
    }

    /**
     * Stahne DB ze serveru na klienta
     * @param context context
     */
    public void downloadDBFromServer(Context context)
    {
        final Replication pull = db.createPullReplication(serverURL);

        final ProgressDialog pd = ProgressDialog.show(context, "Wait....", "Sync in progess", false);
        pull.addChangeListener(new Replication.ChangeListener() {
            @Override
            public void changed(Replication.ChangeEvent changeEvent) {
                boolean active = pull.getStatus() == Replication.ReplicationStatus.REPLICATION_ACTIVE;
                if (!active)
                {
                    pd.dismiss();
                }
                else
                {
                    int total = pull.getCompletedChangesCount();
                    pd.setMax(total);
                    pd.setProgress(pull.getChangesCount());
                }
            }
        });
        pull.start();


    }

    /**
     * Nahraje klientovu DB na server.
     * @param context Context
     */
    public void uploadDBToServer(Context context)
    {
        final Replication push = db.createPushReplication(serverURL);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String cookieName = sp.getString("cookie_name", "SyncGatewaySession");
        String sessionId = sp.getString("session_id", "sss");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int dayToAdd = 1;
        cal.add(Calendar.DATE, dayToAdd);
        Date expires = cal.getTime();

        push.setCookie(cookieName,sessionId, "/", expires, false, false);

        final ProgressDialog pd = ProgressDialog.show(context, "Wait....", "Sync in progess", false);
        push.addChangeListener(new Replication.ChangeListener() {
            @Override
            public void changed(Replication.ChangeEvent changeEvent) {
                boolean active = push.getStatus() == Replication.ReplicationStatus.REPLICATION_ACTIVE;
                if (!active)
                {
                    pd.dismiss();
                }
                else
                {
                    int total = push.getCompletedChangesCount();
                    pd.setMax(total);
                    pd.setProgress(push.getChangesCount());
                }
            }
        });
        push.start();

        System.out.println("Sync Up");

    }

    /**
     * Ziska fingerprint na zaklade ID
     * @param id ID fingerprintu
     * @return Fingerprint s ID
     */
    public Fingerprint getFingerprintById(String id)
    {
        Document doc = db.getDocument(id);
        Fingerprint p = getFingerprintFormDocument(doc);
        return p;
    }

    public boolean existDB(){
        if (db.exists())
            return true;
        else
            return false;
    }

}
