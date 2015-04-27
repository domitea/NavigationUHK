package uhk.kikm.navigationuhk;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Odlehcena verze CollectorActivity urcena pouze ke hledani
 */

public class PrimaryActivity extends ActionBarActivity {

    WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        view = (WebView) findViewById(R.id.webViewPrimary);

        view.getSettings().setBuiltInZoomControls(true); // Zapnuti zoom controls
        view.getSettings().setSupportZoom(true);
        view.setWebViewClient(new WebViewClient());

        view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8"); // nacteni souboru do prohlizece
        Toast.makeText(this, getString(R.string.title_level2), Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_primary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_find) {
            Toast.makeText(this, "Hledam", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_level_1) {
            Toast.makeText(this, getString(R.string.title_level1), Toast.LENGTH_SHORT).show();
            changeLevel(1);
        }
        else if (id == R.id.action_level_2) {
            Toast.makeText(this, getString(R.string.title_level2), Toast.LENGTH_SHORT).show();
            changeLevel(2);
        }
        else if (id == R.id.action_level_3) {
            Toast.makeText(this, getString(R.string.title_level3), Toast.LENGTH_SHORT).show();
            changeLevel(3);
        }
        else if (id == R.id.action_level_4) {
            Toast.makeText(this, getString(R.string.title_level4), Toast.LENGTH_SHORT).show();
            changeLevel(4);
        }
        else if (id == R.id.action_download) {
            Toast.makeText(this, "Stahuji...", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_change_mode) {

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
}
