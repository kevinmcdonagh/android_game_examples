package com.stuffthathappens.games;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.stuffthathappens.games.sensor.Accelerometer;
import com.stuffthathappens.games.sensor.CourseGrainedOrientation;
import com.stuffthathappens.games.sensor.Orientation;

public class Home extends Activity implements OnClickListener {
	
	private Button accelerometerBtn;
	private Button orientationBtn;
	private Button sensorListBtn;
	private Button bouncingBallBtn;
	private Button bubblesBtn;
	private Button	courseOrientationBtn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        accelerometerBtn = (Button) findViewById(R.id.accelerometer_btn);
        accelerometerBtn.setOnClickListener(this);

        orientationBtn = (Button) findViewById(R.id.orientation_btn);
        orientationBtn.setOnClickListener(this);
        
        courseOrientationBtn = (Button) findViewById(R.id.course_orientation_btn);
        courseOrientationBtn.setOnClickListener(this);
        
        sensorListBtn = (Button) findViewById(R.id.sensor_list_btn);
        sensorListBtn.setOnClickListener(this);
        
        bouncingBallBtn = (Button) findViewById(R.id.bouncing_ball_btn);
        bouncingBallBtn.setOnClickListener(this);
        
        bubblesBtn = (Button) findViewById(R.id.bubbles_btn);
        bubblesBtn.setOnClickListener(this);
    } 
    
    public void onClick(View v) {
		if (v == sensorListBtn) {
    		startActivity(new Intent(Home.this, Sensors.class));
    	} else if (v == accelerometerBtn) {
    		startActivity(new Intent(Home.this, Accelerometer.class));
    	} else if (v == orientationBtn) {
    		startActivity(new Intent(Home.this, Orientation.class));
    	} else if (v == courseOrientationBtn) {
    		startActivity(new Intent(Home.this, CourseGrainedOrientation.class));
    	} else if (v == bouncingBallBtn) {
    		startActivity(new Intent(Home.this, BouncingBallActivity.class));
    	} else if (v == bubblesBtn) {
    		startActivity(new Intent(Home.this, BubblesActivity.class));
    	}
    }
}