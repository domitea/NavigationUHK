package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;


/**
 * Trida ziskavajici informace o zarizeni
 * Dominik Matoulek 2015
 */
public class DeviceInformation {

    TelephonyManager telephonyManager;
    Build build;

    /**
     * Inicializuje DeviceInformation
     * @param context context
     */
    public DeviceInformation(Context context) {
        build = new Build();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Naplni fingerprint daty o zarizeni, ktere fingerprint delalo
     * @param p fingerprint vhodny k naplneni daty
     * @return fingerprint s naplenynmi daty
     */
    public Fingerprint fillPosition(Fingerprint p)
    {
        p.setDeviceID(telephonyManager.getDeviceId());
        p.setBoard(build.BOARD);
        p.setBootloader(build.BOOTLOADER);
        p.setBrand(build.BRAND);
        p.setDevice(build.DEVICE);
        p.setDisplay(build.DISPLAY);
        p.setFingerprint(build.FINGERPRINT);
        p.setHardware(build.HARDWARE);
        p.setHost(build.HOST);
        p.setOsId(build.ID);
        p.setManufacturer(build.MANUFACTURER);
        p.setModel(build.MODEL);
        p.setProduct(build.PRODUCT);
        p.setSerial(build.SERIAL);
        p.setTags(build.TAGS);
        p.setType(build.TYPE);
        p.setUser(build.USER);
        return p;
    }
}
