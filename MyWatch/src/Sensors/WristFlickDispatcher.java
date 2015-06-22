package Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class WristFlickDispatcher implements SensorEventListener{
	private float lastY, lastZ;
	private WristFlickListener wristFlickListener;

	/**
	 * Create an instance of this class to start listening to WristFlickEvents.
	 * @param context the context in which the dispatcher will dispatch events.
	 */
	public WristFlickDispatcher(Context context){
		// Set up the accelerometer
				SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
				Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				//Register Sensor:
				sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.values[1] > lastY + 2 && event.values[2] < lastZ - 2){
			if (wristFlickListener != null){
				wristFlickListener.onWristFlick();
			}
		} 
		lastY = event.values[1];
		lastZ = event.values[2];
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	/**
	 * Add your own listener to the dispatcher.
	 * @param listener the listener to be added.
	 */
	public void setListener(WristFlickListener listener){
		this.wristFlickListener = listener;
	}
}
