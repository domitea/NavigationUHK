package uhk.kikm.navigationuhk.utils.LocalizationService;

import uhk.kikm.navigationuhk.model.Position;

/**
 * Created by dominik on 26.2.15.
 */
public class LocalizationService {


    private float stepX;
    private float stepY;

    /**
     * Kosntruktor service na vypocet GPS souradnice.
     * @param point Bod
     *
     */
    public LocalizationService(LocalizationServicePoint point) {

        stepX = point.getLatitude() / point.getX();
        stepY = point.getLongitude() / point.getY();
    }

    /**
     * Vraci bod s vypocitanymi GPS souradnicemi
     * @param p Bod bez GPS souradnic
     * @return Bod s vypocitanimi GPS souradnicemi
     */
    public Position getPoint(Position p)
    {
        p.setLat(p.getY() * stepY);
        p.setLon(p.getX() * stepX);

        return p;
    }

}
