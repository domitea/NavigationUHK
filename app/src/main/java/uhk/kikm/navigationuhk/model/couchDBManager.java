package uhk.kikm.navigationuhk.model;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Trida reprezentujici komunikaci s DB vyuzivajici model (tridy Position a Scan)
 */
public class couchDBManager {
    Context context;
    Manager manager;
    Database db;

    public couchDBManager(Context context) {
        this.context = context;
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);

            this.db = manager.getDatabase("ScanUHK");
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
