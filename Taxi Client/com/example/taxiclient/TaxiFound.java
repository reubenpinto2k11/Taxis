package com.example.taxiclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class TaxiFound extends Activity {
	 private TextView timerValue;
     private long startTime = 0L;
     private Handler customHandler = new Handler();
     long timeInMilliseconds = 0L;
     long timeSwapBuff = 0L;
     long updatedTime = 0L;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_found);
	}
	
	
	
	private Runnable updateTimerThread = new Runnable() {
		
		 
		        public void run() {
		            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
		            updatedTime = timeSwapBuff + timeInMilliseconds;
		            int secs = (int) (updatedTime / 1000);
		            int mins = secs / 60;
		            secs = secs % 60;
		            int milliseconds = (int) (updatedTime % 1000);
		            timerValue.setText("" + mins + ":"
		                    + String.format("%02d", secs) );
		            customHandler.postDelayed(this, 0);
		        }
	};

	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_found, menu);
		return true;
	}

	}
	
