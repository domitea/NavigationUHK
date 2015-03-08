package uhk.kikm.navigationuhk.utils.LocalizationService;

import uhk.kikm.navigationuhk.model.Position;

/**
 * Created by dominik on 26.2.15.
 */
public class LocalizationService {

    private LocalizationServicePoint pointA;
    private LocalizationServicePoint pointB;

    private float stepX;
    private float stepY;

    /**
     * Kosntruktor service na vypocet GPS souradnice.
     * @param pointA Bod A
     * @param pointB Bod B, pricemz B>A
     */
    public LocalizationService(LocalizationServicePoint pointA, LocalizationServicePoint pointB) {
        this.pointA = pointA;
        this.pointB = pointB;

        float differenceX = pointB.getX() - pointA.getX();
        float differenceY = pointB.getY() - pointA.getY();

        stepX = (pointB.getLongitude() - pointA.getLongitude()) / differenceX;
        stepY = (pointB.getLatitude() - pointB.getLatitude()) / differenceY;
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
