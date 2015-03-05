package uhk.kikm.navigationuhk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by dominik on 5.3.15.
 */
public class SensorScanner {

    private Context context;

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private float accX, accY, accZ, gyroX, gyroY, gyroZ, magX, magY, magZ;

    public SensorScanner(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);


    }

    public void initListeners()
    {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                switch (sensor.getType())
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
}
