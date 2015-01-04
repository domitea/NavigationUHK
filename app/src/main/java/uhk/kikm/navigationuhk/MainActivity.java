package uhk.kikm.navigationuhk;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import uhk.kikm.navigationuhk.utils.WebViewInterface;


public class MainActivity extends ActionBarActivity {

    WebViewInterface webInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webInterface = new WebViewInterface(this);

        WebView view = (WebView) findViewById(R.id.WebView);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setSupportZoom(true);
        view.setWebViewClient(new WebViewClient());
        view.loadData(readTextFromResource(R.drawable.uhk_j_2_level), null, "UTF-8");
        view.addJavascriptInterface(webInterface, "Android");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metoda na nacateni textu z nejakeho souboru.
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
