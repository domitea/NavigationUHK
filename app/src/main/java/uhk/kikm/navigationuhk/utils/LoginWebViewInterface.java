package uhk.kikm.navigationuhk.utils;

import android.webkit.JavascriptInterface;

/**
 * Created by dominik on 4.4.15.
 */
public class LoginWebViewInterface {

    private String couhBaseId;
    private String sessionId;
    private String expireTime;
    private String cookieName;

    public LoginWebViewInterface() {

    }

    @JavascriptInterface
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
        System.out.println(sessionId);
    }

    @JavascriptInterface
    public void setCookieName(String cookieName)
    {
        this.cookieName = cookieName;
        System.out.println(cookieName);
    }

    @JavascriptInterface
    public void setExpires(String expires)
    {
        this.expireTime = expires;
        System.out.println(expires);
    }

    @JavascriptInterface
    public void setCouchId(String couchId)
    {
        this.couhBaseId = couchId;
        System.out.println(couchId);
    }
}
