package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import uhk.kikm.navigationuhk.model.Position;


/**
 * Created by dominik on 5.3.15.
 */
public class DeviceInformation {

    TelephonyManager telephonyManager;
    Build build;

    public DeviceInformation(Context context) {
        build = new Build();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public Position fillPosition(Position p)
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
