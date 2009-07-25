package com.stuffthathappens.games.sensor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.TextView;

import com.stuffthathappens.games.R;

public class Orientation extends Activity implements android.hardware.SensorEventListener {

	private SensorManager			sensorMgr;
	private TextView				accuracyLabel;
	private TextView				xLabel, yLabel, zLabel;
	private PowerManager.WakeLock	wl;

	private float					x, y, z;
	private long					lastUpdate	= -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orientation);
		accuracyLabel = (TextView) findViewById(R.id.accuracy_label);
		xLabel = (TextView) findViewById(R.id.x_label);
		yLabel = (TextView) findViewById(R.id.y_label);
		zLabel = (TextView) findViewById(R.id.z_label);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
	}

	@Override
	protected void onPause() {
		super.onPause();
		wl.release();
		sensorMgr.unregisterListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		sensorMgr = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		wl.acquire();
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

		if (!accelSupported) {
			// on accelerometer on this device
			sensorMgr.unregisterListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION));
			accuracyLabel.setText(R.string.no_accelerometer);
		}
	}

	// from the android.hardware.SensorListener interface
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// this method is called very rarely, so we don't have to
		// limit our updates as we do in onSensorChanged(...)
		if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
			switch (accuracy) {
				case SensorManager.SENSOR_STATUS_UNRELIABLE:
					accuracyLabel.setText(R.string.accuracy_unreliable);
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
					accuracyLabel.setText(R.string.accuracy_low);
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
					accuracyLabel.setText(R.string.accuracy_medium);
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
					accuracyLabel.setText(R.string.accuracy_high);
					break;
			}
		}
	}

	// from the android.hardware.SensorListener interface
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			long curTime = System.currentTimeMillis();

			// only allow one update every 100ms, otherwise updates
			// come way too fast and the phone gets bogged down
			// with garbage collection
			if (lastUpdate == -1 || (curTime - lastUpdate) > 100) {
				lastUpdate = curTime;

				x = event.values[SensorManager.DATA_X];
				y = event.values[SensorManager.DATA_Y];
				z = event.values[SensorManager.DATA_Z];

				NumberFormat formatter = new DecimalFormat("000");


				xLabel.setText("X:" + formatter.format(x) + "  (Azimuth)" );
				yLabel.setText("Y:" + formatter.format(y) + "  (Pitch)" );
				zLabel.setText("Z:" + formatter.format(z) + "  (Roll)");
			}
		}
	}

}
