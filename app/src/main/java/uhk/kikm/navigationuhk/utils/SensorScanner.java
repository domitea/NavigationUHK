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
                         // TODO> Make it!
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        // TODO> Make it!
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        // TODO> Make it!
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
