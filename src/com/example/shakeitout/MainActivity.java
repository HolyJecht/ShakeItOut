package com.example.shakeitout;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.shakeitout.MESSAGE";
	private final static int SUCCESS_CRITERION = 100;
	private final static int LEGIT_SHAKE = 2;
	private final static float DIFFICULTY_FACTOR = 1.0f;
	private final static int PER25 = 25;
	private final static int PER50 = 50;
	private final static int PER75 = 75;
	private final static int PER100 = 100;
	private final static int VIBRATE_ENCOURAGE = 500;
	private final static int VIBRATE_FINISH = 1000;
	
	private boolean encourage[] = new boolean[3];
	
	private long currentTime, currentStart;
	private boolean gameStop = false;
	private ProgressBar progressBar1;
	private TextView textView1, textView2;
	private Button button1;
	private ImageView imageView1;
	private MediaPlayer mp;
	private Vibrator vb;
	private RelativeLayout layout;
	
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
	    	
	    	// Get instance of Vibrator from current Context
	    	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

	    	
	    	if(!encourage[0] && progressBar1.getProgress() >= PER25) {
	    		v.vibrate(VIBRATE_ENCOURAGE); 
	    		playSound(PER25);
	    		encourage[0] = true;
	    	}
	    	
	    	if(progressBar1.getProgress() >= PER50) {
	    		
	    	}
	    		
	    	
	    	if(!encourage[1] && progressBar1.getProgress() >= PER75) {
	    		v.vibrate(VIBRATE_ENCOURAGE); 
	    		playSound(PER75);
	    		encourage[1] = true;
	    	}
	    	
			if (!encourage[2] && progressBar1.getProgress() >= SUCCESS_CRITERION) {
				v.vibrate(VIBRATE_FINISH); 
				playSound(PER100);
				encourage[2] = true;
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
        textView2 = (TextView) findViewById(R.id.textView2);        
        layout = (RelativeLayout) findViewById(R.id.myLayout);
        //layout = new RelativeLayout(this);
	}
	
	protected void showNewGameView() {		
		imageView1.setVisibility(View.INVISIBLE);
		button1.setVisibility(View.INVISIBLE);
		textView2.setText("");
		textView2.setVisibility(View.INVISIBLE);
		layout.setBackgroundResource(R.drawable.ketchup_bottle_2);
		gameStop = false;
		currentTime = 0;
		currentStart = System.currentTimeMillis();
		Log.w("time", String.valueOf(currentStart));
	}
	
	protected void showSuccessView() {
		gameStop = true;
		clearProgress();
		layout.setBackgroundResource(R.drawable.sad_face);
		//imageView1.setVisibility(View.VISIBLE);
		button1.setVisibility(View.VISIBLE);
		textView2.setVisibility(View.VISIBLE);
		currentTime += System.currentTimeMillis() - currentStart;
		textView2.setText("Your score: " + currentTime / 1000 + " seconds!");
				
		/*Intent intent = new Intent(this, DisplayMessageActivity.class);
		String message = "Bukkake Awarded";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);*/
	}
	
	protected void clearProgress() {
		for(int i = 0; i < encourage.length; i++) encourage[i] = false;
		progressBar1.setProgress(0);
	}
	
	protected void playSound(int progress) {
		String filename = "yes.mp3";
		switch (progress) {
			case PER25:
				filename = "try.mp3";
				break;
			case PER75:
				filename = "wand.mp3";
				break;
			case PER100:
				filename = "yes.mp3";
				break;
		}
		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
			
			AssetFileDescriptor afd = getAssets().openFd(filename);			
			mp = new MediaPlayer();
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer m) {
					// TODO Auto-generated method stub
					m.release();
				}

			});
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			Log.w("Error", e.getMessage());
		}
	}
	
    public void startNewGame(View view) {
	    showNewGameView();
    }    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        
        initializeUIElements();
        showSuccessView();
        imageView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        
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
		currentStart = System.currentTimeMillis();		
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mSensorListener);
		currentTime += System.currentTimeMillis() - currentStart;		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }          
}
