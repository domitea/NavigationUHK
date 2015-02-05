package uhk.kikm.navigationuhk.model;

/**
 * Modelova trida reprezentujci scan
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
}
