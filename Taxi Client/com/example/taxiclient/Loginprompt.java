package com.example.taxiclient;


import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Button;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class Loginprompt extends Activity {
	TextView t;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginprompt);
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		//gps
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
		 boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		//mobile
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

		//wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		
		if ((mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED)&& statusOfGPS==true)
		{
			Button button;
			SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);


			//check for already logged in
			if(!spref.getString("username", "XYZZY").equals("XYZZY")) //usr already logged in. show register activity
			{
				Intent a = new Intent(Loginprompt.this, MainActivity.class);
				startActivity(a);
			}



			button = (Button) findViewById(R.id.buttonYes);

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Intent intent = new Intent(Loginprompt.this, LoginActivity.class);
					startActivity(intent);   

				}

			});





			TextView newusr=(TextView) findViewById(R.id.textView1);
			
			newusr.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
				Intent x= new Intent(Loginprompt.this,Register.class);
				startActivity(x);
				}
				
				});

		} 
		else
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	 
				// set title
				alertDialogBuilder.setTitle("NO INTERNET CONNECTION AND/OR GPS");
	 
				// set dialog message
				alertDialogBuilder
					.setMessage("Click yes to exit!")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							Loginprompt.this.finish();
							System.exit(1);
						}
					  })
					.setNegativeButton("retry",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
					
                            Intent i=new Intent(getApplicationContext(),Loginprompt.class);
                            startActivity(i);
                             	dialog.cancel();
							
							
						}
					});
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
		}
		
		



	}



	public void onClick(View arg0) {
		//for clickable textview
		Intent intent = new Intent(Loginprompt.this, Register.class);
		startActivity(intent);

	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loginprompt, menu);
		return true;
	}





