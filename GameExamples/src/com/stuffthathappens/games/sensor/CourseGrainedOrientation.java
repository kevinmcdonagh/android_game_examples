package com.stuffthathappens.games.sensor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.TextView;

import com.stuffthathappens.games.R;

public class CourseGrainedOrientation extends Activity {

	public static int			sCurrentRotation;
	public OrientaionStatusView	mView;
	private OnRotationEvent		mOnRotationEvent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = new OrientaionStatusView(this);
		mOnRotationEvent = new OnRotationEvent(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOnRotationEvent.enable();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mOnRotationEvent.disable();
	}

	private class OnRotationEvent extends OrientationEventListener {

		private static final String	TOP				= "UPRIGHT";
		private static final String	RIGHT			= "TILTED RIGHT";
		private static final String	BOTTOM			= "UPSIDE DOWN";
		private static final String	LEFT			= "TILTED LEFT";
		private static final String	FLAT			= "LYING FLAT";
		private long				lastUpdate		= -1;
		private String				currentSector	= TOP;

		public OnRotationEvent(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			long curTime = System.currentTimeMillis();

			if (lastUpdate == -1 || (curTime - lastUpdate) > 100) {
				sCurrentRotation = orientation;
				mView.setOrientaion(orientation);

				String sector = getCurrentSector(orientation);
				if (!currentSector.equals(sector)) {
					mView.setSector(sector);
					currentSector = sector;
				}

				lastUpdate = curTime;
			}
		}

		private String getCurrentSector(int orientation) {

			boolean top = orientation > 350 || orientation < 10 && orientation != -1;
			boolean right = orientation > 10 && orientation < 170;
			boolean bottom = orientation > 170 && orientation < 190;
			boolean left = orientation > 190 && orientation < 350;

			if (top) {
				return OnRotationEvent.TOP;
			} else if (right) {
				return OnRotationEvent.RIGHT;
			} else if (bottom) {
				return OnRotationEvent.BOTTOM;
			} else if (left) {
				return OnRotationEvent.LEFT;
			}

			return OnRotationEvent.FLAT;
		}

	}

	private class OrientaionStatusView extends View {

		public TextView	mCurrentOrientation_lbl;
		public TextView	mSector_lbl;

		public OrientaionStatusView(Context context) {
			super(context);
			final View orientationView = LayoutInflater.from(context).inflate(R.layout.easy_orientation, null);
			setContentView(orientationView);
			mCurrentOrientation_lbl = (TextView) orientationView.findViewById(R.id.orientation_label);
			mSector_lbl = (TextView) orientationView.findViewById(R.id.current_sector);
		}

		public synchronized void setOrientaion(int orientation) {
			mCurrentOrientation_lbl.setText("Orientation: " + orientation);
		}

		public synchronized void setSector(String sector) {
			mSector_lbl.setText("Sector: " + sector);
		}
	}

}
