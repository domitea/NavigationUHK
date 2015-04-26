package uhk.kikm.navigationuhk;

import uhk.kikm.navigationuhk.utils.LocalizationService.LocalizationServicePoint;

/**
 *  Trida obsahujici konfiguracni hodnoty
 *
 *  Dominik Matoulek 2015
 */
public class SettingsFactory {
    public static LocalizationServicePoint pointA = new LocalizationServicePoint(34,31,new Float(50.2045875),new Float(15.8290822));
    public static LocalizationServicePoint pointB = new LocalizationServicePoint(642,39,new Float(50.2045247),new Float(15.8297047));
    public static LocalizationServicePoint pointC = new LocalizationServicePoint(26,794, new Float(50.2040850), new Float(15.8289544));

    public static String LOGIN_URL = "http://node.ukuree.cz";
    public static String DB_URL = "http://db.ukuree.cz/gw/";
}
