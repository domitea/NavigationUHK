package uhk.kikm.navigationuhk.model;

import android.content.Context;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Trida reprezentujici komunikaci s DB vyuzivajici model (tridy Position a Scan)
 */
public class couchDBManager {
    Context context;
    Manager manager;

    public couchDBManager(Context context) {
        this.context = context;
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
