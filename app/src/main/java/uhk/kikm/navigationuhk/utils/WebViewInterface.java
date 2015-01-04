package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.webkit.JavascriptInterface;

/** Trida reprezetntujici interface k WebView
 *
 */
public class WebViewInterface {

    Context context;

    int x;
    int y;

    boolean changed = false;

    public WebViewInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void setXY(int x, int y)
    {
        this.x = x;
        this.y = y;

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
