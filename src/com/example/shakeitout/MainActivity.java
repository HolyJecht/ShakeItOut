package com.example.shakeitout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.shakeitout.MESSAGE";
	private final static int SUCCESS_CRITERION = 100;
	private final static int LEGIT_SHAKE = 2;
	private final static float DIFFICULTY_FACTOR = 0.8f;
	
	private boolean gameStop = false;
	private ProgressBar progressBar1;
	private TextView textView1;
	private Button button1;
	private ImageView imageView1;
	
	/* put this into your activity class */
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
						
	    	textView1.setText(Float.toString(mAccel));
	    	if(Math.abs(mAccel) > LEGIT_SHAKE && !gameStop)
	    		progressBar1.setProgress(progressBar1.getProgress() + Math.round(Math.abs(mAccel) * DIFFICULTY_FACTOR));
	    	
			if (progressBar1.getProgress() >= SUCCESS_CRITERION) {
				progressBar1.setProgress(0);
				showSuccessView();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	
	protected void initializeUIElements() {
        textView1 = (TextView) findViewById(R.id.textView1);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        button1 = (Button) findViewById(R.id.button1);
        imageView1 = (ImageView) findViewById(R.id.test_image);
	}
	
	protected void showNewGameView() {
		progressBar1.setProgress(0);		
		imageView1.setVisibility(View.INVISIBLE);
		button1.setVisibility(View.INVISIBLE);
		gameStop = false;
	}
	
	protected void showSuccessView() {
		gameStop = true;
		imageView1.setVisibility(View.VISIBLE);
		button1.setVisibility(View.VISIBLE);
		
		/*Intent intent = new Intent(this, DisplayMessageActivity.class);
		String message = "Bukkake Awarded";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);*/
	}
	
    public void startNewGame(View view) {
	    showNewGameView();
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeUIElements();
        showSuccessView();
        imageView1.setVisibility(View.INVISIBLE);
        
        /* do this in onCreate */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }          
}
