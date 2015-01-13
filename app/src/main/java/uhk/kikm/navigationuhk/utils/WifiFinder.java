package uhk.kikm.navigationuhk.utils;

import android.net.wifi.ScanResult;

import java.util.ArrayList;

import uhk.kikm.navigationuhk.model.Level;
import uhk.kikm.navigationuhk.model.Position;

/**
 * Trida reprezentujici vyhledavani polohy
 */
public class WifiFinder {
    private Level level;

    public WifiFinder(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Position getPosition(ScanResult scanForIdentify)
    {

    }
}
