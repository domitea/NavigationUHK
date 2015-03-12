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
     * @param pointA Bod
     *
     */
    public LocalizationService(LocalizationServicePoint pointA, LocalizationServicePoint pointB) {

        float differenceX = pointB.getX() - pointA.getX();
        float differenceY = pointB.getY() - pointA.getY();

        stepX = (pointB.getLongitude() - pointA.getLongitude()) / differenceX;
        stepY = (pointB.getLatitude() - pointA.getLatitude()) / differenceY;
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
