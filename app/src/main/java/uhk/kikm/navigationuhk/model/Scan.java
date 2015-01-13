package uhk.kikm.navigationuhk.model;

/**
 * Modelova trida reprezentujci scan
 */
public class Scan {
    String SSID;
    String MAC;
    int strentgh;

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

    public int getStrentgh() {
        return strentgh;
    }

    public void setStrentgh(int strentgh) {
        this.strentgh = strentgh;
    }
}
