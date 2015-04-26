package uhk.kikm.navigationuhk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uhk.kikm.navigationuhk.utils.LoginWebViewInterface;

/**
 * Aktivita zajistujici prihlasovani uzivatelu
 *
 * Dominik Matoulek 2015
 */

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginWebViewInterface loginInterface = new LoginWebViewInterface(this);

        WebView webView = (WebView) findViewById(R.id.webViewLogin);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(SettingsFactory.loginURL);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(loginInterface, "Android");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * Metoda volana z loginInterface
     * @param cookieName Jmeno cookie
     * @param sessionId id Session
     * @param expireTime Cas vyprseni
     * @param couchBaseId id uzivatele - Pro ulozeni k porizovanemu Fingerprintu
     */
    public void run(String cookieName, String sessionId, String expireTime, String couchBaseId)
    {
        // ulozeni dat na perzistentni misto - SharedPreferences, ktere jsou poskytovany kazde aplikaci

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("couchbase_sync_gateway_id", couchBaseId); // vlozeni dat
        editor.putString("cookie_name", cookieName);
        editor.putString("session_id", sessionId);
        editor.putString("expire_time", expireTime);

        editor.commit(); // ulozeni dat

        Intent intent = new Intent(this, CollectorActivity.class);
        startActivity(intent);
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
}
