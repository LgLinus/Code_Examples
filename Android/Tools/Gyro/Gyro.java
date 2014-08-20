package com.controlforconsol;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class responsible of handling the gyroscope in the andorid device
 * 
 * @author Linus Granath
 * 
 */
public class Gyro {

	public Gyro(final Controller controller) {
		Thread thread = new Thread() {
			public void run() {
				new GyroSender(controller);
			}
		};
		thread.start();
	}

	// Class responsible of
	private class GyroSender implements SensorEventListener {

		private Controller controller;
		private double previousAngle;
		private int startAngle = 170;
		private boolean enabled = true;
		private boolean first = true;
		private int sensitivity = 15;

		@SuppressWarnings("deprecation")
		public GyroSender(Controller controller) {
			this.controller = controller;
			SensorManager mSensorManager = (SensorManager) controller
					.getActivity().getSystemService(Context.SENSOR_SERVICE);
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_FASTEST);

		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		// When a value updates on the gyro the code below will execute
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (enabled) {
				if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
					return;
				}
				float zy_angle = event.values[2];
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (first) {
					startAngle = (int) zy_angle;
					first = false;
					previousAngle = startAngle;
				}
				double angle = zy_angle;
				angle = startAngle - angle;
				if ((previousAngle - angle) < -sensitivity
						|| ((previousAngle - angle) > sensitivity)) {// If the device have been tilted at least @var sensitivity since we sent last message,e send a new one and update the angle

					
					controller.getSendThread().send("Gyro:" + (int) angle);
					previousAngle = angle;
				}
			}
		}

	}
}
