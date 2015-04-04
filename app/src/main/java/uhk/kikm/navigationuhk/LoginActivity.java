package uhk.kikm.navigationuhk;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uhk.kikm.navigationuhk.utils.LoginWebViewInterface;


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
        System.out.println(this.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void run()
    {
        System.out.println("call");
        System.out.println(this.toString());
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
