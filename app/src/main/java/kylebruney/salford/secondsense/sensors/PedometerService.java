package kylebruney.salford.secondsense.sensors;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import kylebruney.salford.secondsense.util.Constants;

public class PedometerService extends Service implements SensorEventListener {

	private ResultReceiver mReceiver;
	private SensorManager sensorManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mReceiver = intent.getParcelableExtra(Constants.EXTRA_RECEIVER);

		return START_STICKY;

	}

	@Override
	public void onCreate() {
		// Setup and start collecting
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
				SensorManager.SENSOR_DELAY_FASTEST);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// Stop collecting and tear down
		sensorManager.unregisterListener(this);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final String TAG = PedometerService.class.getSimpleName();

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
			Sensor sensor = event.sensor;
			float[] values = event.values;
			// Movement
			float x = -1;

			
			if(values.length > 0) {
				x = (int) values[0];
			}


			if (mReceiver != null) {
				if(sensor.getType() == Sensor.TYPE_STEP_COUNTER) {				
				Bundle result = new Bundle();
				result.putFloat(PedometerResultReceiver.EXTRA_X, x);
				mReceiver.send(PedometerResultReceiver.RESULTCODE_UPDATE,
						result);
				}
			}
		}
	}

}