package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import uhk.kikm.navigationuhk.dataLayer.Fingerprint;

/**
 * Trida reprezentujici ziskavani dat ze senzoru telefonu pro zjisteni orientace v prostoru
 * Dominik Matoulek 2015
 */
public class SensorScanner {

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private float accX, accY, accZ, gyroX, gyroY, gyroZ, magX, magY, magZ;

    /**
     * Inicializuje SensorScanner
     * @param context context
     */
    public SensorScanner(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        initListeners();

        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Inicializuje listernery
     */
    private void initListeners()
    {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                switch (sensor.getType()) // Pokud je typ sensoru:
                {
                    case Sensor.TYPE_ACCELEROMETER:
                        accX = event.values[0];
					    accY = event.values[1];
					    accZ = event.values[2];
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        gyroX = event.values[0];
					    gyroY = event.values[1];
					    gyroZ = event.values[2];
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magX = event.values[0];
					    magY = event.values[1];
					    magZ = event.values[2];
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /**
     * Naplni fingerprint daty ze senzoru
     * @param p Fingerprint k naplneni
     * @return fingerprint naplneny daty
     */
    public Fingerprint fillPosition(Fingerprint p)
    {
        p.setAccX(accX);
        p.setAccY(accY);
        p.setAccZ(accZ);

        p.setGyroX(gyroX);
        p.setGyroY(gyroY);
        p.setGyroZ(gyroZ);

        p.setMagX(magX);
        p.setMagY(magY);
        p.setMagZ(magZ);
        return p;
    }
}
