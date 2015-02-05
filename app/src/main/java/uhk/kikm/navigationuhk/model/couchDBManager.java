package uhk.kikm.navigationuhk.model;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Trida reprezentujici komunikaci s DB vyuzivajici model (tridy Position a Scan)
 */
public class couchDBManager {
    Context context;
    Manager manager;
    Database db;

    final String dbname = "Scan_uhk";
    final String viewByMac = "by_mac";

    public couchDBManager(Context context) {
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
    }

    public void savePositions(List<Position> positions)
    {
        for (Position p : positions){
            Map<String, Object> properties = new HashMap<>();
            properties.put("x", String.valueOf(p.getX()));
            properties.put("y", String.valueOf(p.getY()));
            properties.put("level", String.valueOf(p.getLevel()));
            properties.put("description", p.getDescription());

            List<Map<String, Object>> scansArray = new ArrayList<>();
            ArrayList<Scan> scans = p.getScans();
            for (Scan s : scans)
            {
                Map<String, Object> scanProperties = new HashMap<>();
                scanProperties.put("ssid",s.getSSID());
                scanProperties.put("mac",s.getMAC());
                scanProperties.put("strenght",String.valueOf(s.getStrenght()));

                scansArray.add(scanProperties);
            }

            properties.put("scans",scansArray);

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
            Map<String, Object> properties = new HashMap<>();
            properties.put("x", String.valueOf(p.getX()));
            properties.put("y", String.valueOf(p.getY()));
            properties.put("level", String.valueOf(p.getLevel()));
            properties.put("description", p.getDescription());

            List<Map<String, Object>> scansArray = new ArrayList<>();
            ArrayList<Scan> scans = p.getScans();
            for (Scan s : scans)
            {
                Map<String, Object> scanProperties = new HashMap<>();
                scanProperties.put("ssid",s.getSSID());
                scanProperties.put("mac",s.getMAC());
                scanProperties.put("strenght",String.valueOf(s.getStrenght()));

                scansArray.add(scanProperties);
            }

            properties.put("scans",scansArray);

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
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
    }

    public List<Position> getAllPositions()
    {
        Query query = db.createAllDocumentsQuery();

        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); )
            {
                QueryRow row = it.next();
                System.out.println(row.getDocument().toString()); // TODO: Dopsat parsovani...
            }
        }
        catch (CouchbaseLiteException cle)
        {
            cle.printStackTrace();
        }
        return new ArrayList<>();

    }


}
