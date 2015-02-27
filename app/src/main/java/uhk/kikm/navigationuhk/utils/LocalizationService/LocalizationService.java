package uhk.kikm.navigationuhk.utils.LocalizationService;

/**
 * Created by dominik on 26.2.15.
 */
public class LocalizationService {

    private LocalizationServicePoint pointA;
    private LocalizationServicePoint pointB;

    private float stepX;
    private float stepY;

    public LocalizationService(LocalizationServicePoint pointA, LocalizationServicePoint pointB) {
        this.pointA = pointA;
        this.pointB = pointB;

        float differenceX = pointB.getX() - pointA.getX();
        float differenceY = pointB.getY() - pointA.getY();

        stepX = (pointB.getLongitude() - pointA.getLongitude()) / differenceX;
        stepY = (pointB.getLatitude() - pointB.getLatitude()) / differenceY;
    }

    public LocalizationServicePoint getPoint(LocalizationServicePoint point)
    {
        point.setLatitude(point.getY() * stepY);
        point.setLongitude(point.getX() * stepX);

        return point;
    }

}
