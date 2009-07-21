package com.stuffthathappens.games.sensor;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.DATA_Z;
import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
import static android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.stuffthathappens.games.R;

/**
 * Displays values from the accelerometer sensor.
 * 
 * @author Eric M. Burke
 */
public class Accelerometer extends Activity implements android.hardware.SensorEventListener, OnClickListener {
	private SensorManager			sensorMgr;
	private TextView				accuracyLabel;
	private TextView				xLabel, yLabel, zLabel;
	private Button					calibrateButton;
	private PowerManager.WakeLock	wl;

	private float					x, y, z;

	// deltas for calibration
	private float					cx, cy, cz;

	private long					lastUpdate	= -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accel);
		accuracyLabel = (TextView) findViewById(R.id.accuracy_label);
		xLabel = (TextView) findViewById(R.id.x_label);
		yLabel = (TextView) findViewById(R.id.y_label);
		zLabel = (TextView) findViewById(R.id.z_label);
		calibrateButton = (Button) findViewById(R.id.calibrate_button);
		calibrateButton.setOnClickListener(this);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
	}

	@Override
	protected void onPause() {
		super.onPause();
		wl.release();
		sensorMgr.unregisterListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		sensorMgr = null;

		cx = 0;
		cy = 0;
		cz = 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
		wl.acquire();
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

		if (!accelSupported) {
			// on accelerometer on this device
			sensorMgr.unregisterListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			accuracyLabel.setText(R.string.no_accelerometer);
		}
	}

	public void onClick(View v) {
		if (v == calibrateButton) {
			cx = -x;
			cy = -y;
			cz = -z;
		}
	}
	
	// from the android.hardware.SensorListener interface
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// this method is called very rarely, so we don't have to
		// limit our updates as we do in onSensorChanged(...)
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
		Log.i("BAM", "Hello");
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			
			// only allow one update every 100ms, otherwise updates
			// come way too fast and the phone gets bogged down
			// with garbage collection
			if (lastUpdate == -1 || (curTime - lastUpdate) > 100) {
				Log.i("BAM", "time[" + curTime +"], X:[" + event.values[DATA_X]+ "] Y:["+event.values[DATA_Y]+"] Z:["+event.values[DATA_Z]+"]");
				lastUpdate = curTime;

				x = event.values[DATA_X];
				y = event.values[DATA_Y];
				z = event.values[DATA_Z];

				xLabel.setText(String.format("X: %+2.5f (%+2.5f)", (x + cx), cx));
				yLabel.setText(String.format("Y: %+2.5f (%+2.5f)", (y + cy), cy));
				zLabel.setText(String.format("Z: %+2.5f (%+2.5f)", (z + cz), cz));
			}
		}
	}
}