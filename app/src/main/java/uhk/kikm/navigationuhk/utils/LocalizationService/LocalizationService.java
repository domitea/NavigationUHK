package uhk.kikm.navigationuhk.utils.LocalizationService;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;

/**
 * Trida starajici se o dopocitani GPS na zaklade souradnic bodu, kde uzivatel klikl
 * Dominik Matoulek 2015
 */
public class LocalizationService {


    /**
     * LAT = Y, LON = X
     */

    private float differenceX;
    private float differenceY;

    private float lonDifference;
    private float latDifference;

    private LocalizationServicePoint point;

    /**
     * Kosntruktor service na vypocet GPS souradnice
     * @param pointA Bod A - hlavni bod
     * @param pointB Bod B - Bod urcujici zemepisnou delku
     * @param pointC Bod C - Bod urcujici zemepisnou sirku
     */
    public LocalizationService(LocalizationServicePoint pointA, LocalizationServicePoint pointB, LocalizationServicePoint pointC) {

        this.point = pointA;

        differenceX = pointB.getX() - pointA.getX();

        differenceY = pointC.getY() - pointA.getY();

        lonDifference = pointB.getLongitude() - pointA.getLongitude();

        // lattitude jde od rovniku, cili pocita se opacne -> A - C misto C - A --> A>C

        latDifference = pointA.getLatitude() - pointC.getLatitude();
    }

    /**
     * Vraci bod s vypocitanymi GPS souradnicemi
     * @param p Bod bez GPS souradnic
     * @return Bod s vypocitanimi GPS souradnicemi
     */
    public Fingerprint getPoint(Fingerprint p)
    {

        float longtitudeP = ( ( (p.getX() - point.getX()) / (differenceX) ) * lonDifference ) + point.getLongitude();
        float latitudeP = point.getLatitude() - ( ( ( p.getY() - point.getY() ) / differenceY ) * latDifference );

        p.setLon(longtitudeP);
        p.setLat(latitudeP);
        return p;
    }

}
