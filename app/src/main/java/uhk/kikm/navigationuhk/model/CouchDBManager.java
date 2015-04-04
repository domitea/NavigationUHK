package uhk.kikm.navigationuhk.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

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
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.auth.AuthenticatorImpl;
import com.couchbase.lite.replicator.Replication;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;

/**
 * Trida reprezentujici komunikaci s DB vyuzivajici model (tridy Position a Scan)
 */
public class CouchDBManager {
    Context context;
    Manager manager;
    Database db;
    URL serverURL;

    final String dbname = "scan_uhk";
    final String viewByMac = "by_mac";
    final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public CouchDBManager(Context context) {
        this.context = context;
        try {

            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS); // pripojeni k DB
            this.db = manager.getDatabase(dbname); // Vybrani/vytvoreni DB

            /**
             * "Deklarace" mapovaci fce.
             *
             * Vytvor seznam klic:hodnota, kde klic je MAC a hodnota je poloha, kde byla zaznamenana ->
             * poloha tam bude n-krat, kde n je pocet zaznamenanych MAC.
             *
             * Vypada to jako redudance, ale pres Query se tak daji vytahnout jen zaznamy prislusici jedne MAC.
             */
            db.getView(viewByMac).setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    List<Map<String, Object>> scans = (List) document.get("scans");
                    for (Map<String, Object> scan : scans)
                    {
                        emitter.emit(scan.get("mac"), document);
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

        try {
            serverURL = new URL("http://BestAdrressCreatedEver.yeah:65536");
        }catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        }
    }

    public void savePositions(List<Position> positions)
    {
        for (Position p : positions){
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


    public void savePosition(Position p)
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

    public void deleteAll()
    {
        try
        {
            db.delete();
            this.db = manager.getDatabase(dbname); // Vybrani/vytvoreni DB

            /**
             * "Deklarace" mapovaci fce. Popsana o na radku 45
             *
             */
            db.getView(viewByMac).setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    List<Map<String, Object>> scans = (List) document.get("scans");
                    for (Map<String, Object> scan : scans)
                    {
                        emitter.emit(scan.get("mac"), document);
                    }
                }
            }, "1");
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
    }

    public List<Position> getPostionsByMac(String mac)
    {
        ArrayList<Position> positions = new ArrayList<>();
        Query query = db.getView(viewByMac).createQuery();

        query.setStartKey(mac);
        query.setEndKey(mac);

        try
        {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                Document doc = row.getDocument();

                positions.add(getPositionFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return positions;
    }

    public List<Position> getPostionsByMacs(String[] macs)
    {
        ArrayList<Position> positions = new ArrayList<>();
        Query query = db.getView(viewByMac).createQuery();

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

                positions.add(getPositionFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return positions;
    }

    public List<Position> getAllPositions()
    {
        Query query = db.createAllDocumentsQuery();
        ArrayList<Position> positions = new ArrayList<>();

        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                Document doc = row.getDocument();

                positions.add(getPositionFormDocument(doc));
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return positions;

    }

    public void removePosition(String id)
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

    private String getCurrentTime()
    {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date today = GregorianCalendar.getInstance().getTime();

        return df.format(today);
    }

    private Date getDate(String date)
    {
        DateFormat df = new SimpleDateFormat(dateFormat);
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

    private Map<String, Object> getMapOfDocument(Position p) {
        Map<String, Object> properties = new HashMap<>();
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
        return properties;
    }

    private Position getPositionFormDocument(Document doc)
    {
        Position p = new Position();
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
        return p;
    }

    private String parseProperty(String property, Document doc)
    {
        Object o = doc.getProperty(property); // V chapani JSON muze byt null !!
        if (o == null)
        {
            return "0";
        }
        return o.toString();
    }

    public void closeConnection()
    {
        db.close();
        manager.close();
    }

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

    public void uploadDBToServer(Context context)
    {
        final Replication push = db.createPushReplication(serverURL);

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


    }

    public Position getPositionById(String id)
    {
        Document doc = db.getDocument(id);
        Position p = getPositionFormDocument(doc);
        return p;
    }

}
