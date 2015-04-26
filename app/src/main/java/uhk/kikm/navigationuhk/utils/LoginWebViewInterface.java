package uhk.kikm.navigationuhk.utils;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import uhk.kikm.navigationuhk.LoginActivity;

/**
 * Trida reprezentujici JavascriptInterface pro LoginActivity
 * Dominik Matoulek 2015
 */
public class LoginWebViewInterface {

    private String couhBaseId;
    private String sessionId;
    private String expireTime;
    private String cookieName;

    private LoginActivity activity;

    public LoginWebViewInterface(LoginActivity activity) {
        this.activity = activity;
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
        activity.run(cookieName, sessionId, expireTime, couhBaseId);
    }
}
