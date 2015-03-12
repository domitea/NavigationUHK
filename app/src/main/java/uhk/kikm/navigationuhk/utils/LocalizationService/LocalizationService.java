package uhk.kikm.navigationuhk.utils.LocalizationService;

import uhk.kikm.navigationuhk.model.Position;

/**
 * Created by dominik on 26.2.15.
 */
public class LocalizationService {


    /**
     * LAT = Y, LON = X
     */

    private float stepX;
    private float stepY;

    private float differenceX;
    private float differenceY;

    private float lonDifference;
    private float latDifference;

    private LocalizationServicePoint point;

    /**
     * Kosntruktor service na vypocet GPS souradnice.
     *
     */
    public LocalizationService(LocalizationServicePoint pointA, LocalizationServicePoint pointB, LocalizationServicePoint pointC) {

        this.point = pointA;

        differenceX = pointB.getX() - pointA.getX();

        differenceY = pointC.getY() - pointA.getY();

        lonDifference = pointB.getLongitude() - pointA.getLongitude();

        // lattitude jde od rovniku, cili pocita se opacne -> A - C misto C - A --> A>C

        latDifference = pointA.getLatitude() - pointC.getLongitude();
    }

    /**
     * Vraci bod s vypocitanymi GPS souradnicemi
     * @param p Bod bez GPS souradnic
     * @return Bod s vypocitanimi GPS souradnicemi
     */
    public Position getPoint(Position p)
    {

        float longtitudeP = ( ( (p.getX() - point.getX()) / (differenceX) ) * lonDifference ) + point.getLongitude();
        float latitudeP = point.getLatitude() - ( ( ( p.getY() - point.getY() ) / differenceY ) * latDifference );

        p.setLon(longtitudeP);
        p.setLat(latitudeP);
        return p;
    }

}
