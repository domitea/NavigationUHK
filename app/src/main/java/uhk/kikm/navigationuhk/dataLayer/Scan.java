package uhk.kikm.navigationuhk.dataLayer;

/**
 * Modelova trida reprezentujci scan Wifi site
 * Dominik Matoulek 2015
 */
public class Scan {
    String SSID;
    String MAC;
    int strenght;

    public Scan(String SSID, String MAC, int strenght) {
        this.SSID = SSID;
        this.MAC = MAC;
        this.strenght = strenght;
    }

    public Scan() {
    }

    public String getSSID() {

        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public int getStrenght() {
        return strenght;
    }

    public void setStrentgh(int strenght) {
        this.strenght = strenght;
    }

    @Override
    public String toString() {
        return "Scan{" +
                "SSID='" + SSID + '\'' +
                ", MAC='" + MAC + '\'' +
                ", strenght=" + strenght +
                '}';
    }
}
