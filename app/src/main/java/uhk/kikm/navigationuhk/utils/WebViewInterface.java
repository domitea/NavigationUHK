package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.webkit.JavascriptInterface;

/** Trida reprezetntujici interface k WebView - zde se zjistuje souradnice bodu na mape, kde uzivatel klikl
 * Dominik Matoulek 2015
 */
public class WebViewInterface {

    Context context;

    int x;
    int y;

    boolean changed = false;

    /**
     * Inicializuje WebViewInterface
     * @param context
     */
    public WebViewInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void setXY(int x, int y)
    {
        this.x = x;
        this.y = y;

        System.out.println(x + " " + y);

        changed = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
