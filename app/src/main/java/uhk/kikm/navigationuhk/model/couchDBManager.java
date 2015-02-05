package uhk.kikm.navigationuhk.model;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Trida reprezentujici komunikaci s DB vyuzivajici model (tridy Position a Scan)
 */
public class couchDBManager {
    Context context;
    Manager manager;
    Database db;

    final String dbname = "ScanUHK";
    final String viewByMac = "byMac";

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
             * Vypada to jako redudance, ale pres Querry se tak daji vytahnout jen zaznamy prislusici jedne MAC.
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
}
