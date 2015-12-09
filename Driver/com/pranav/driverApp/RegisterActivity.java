package com.pranav.driverApp;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;






import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pranav.driverApp.R;

public class RegisterActivity extends Activity {

	Button btnGCMRegister;
	Button btnAppShare;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	//private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";
	static String Dutytext="Go on duty"; 
	static String username="";
	static String password=""; 
	public   String onoff="";
	static int flag=0;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		context = getApplicationContext();


		//get current duty status
		SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
		Editor e=spref.edit();
	//	e.putBoolean("duty",false);
		e.putString("drvstat", "Go on Duty");
		e.commit();
		Button ck=(Button)findViewById(R.id.OnOffDuty);
		
		if(!spref.getBoolean("duty", false))
		{
			ck.setText("Go on Duty");
		}
		else
			ck.setText("Go Off Duty");
			
		

		if(spref.getString("username", "XYZZY").equals("XYZZY")) //usr already logged in. show register activity
		{
			Intent a = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(a);
		}

		username=spref.getString("username","CAN'T HAPPEN");
		password=spref.getString("password","CAN'T HAPPEN");

		//logout button
		Button Logout = (Button) findViewById(R.id.logout);
		Logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				Editor x= spref.edit();
				String username= spref.getString("username","");
				String password=spref.getString("password", "");
    		 
    		 
    		 String url="http://testapp1pranav.appspot.com/markdriver?email="+username+"&pass="+password+"&state=busy";  

				Editor editor = spref.edit();
				editor.putString("url", url);
				editor.putBoolean("duty", false);
				editor.commit();

				

				Thread t1 = new Thread()
				{
					public void run()
					{

						//send to server
						try
						{
							SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

							String url = spref.getString("url", "");

							URL obj = new URL(url);
							HttpURLConnection con = (HttpURLConnection) obj.openConnection();

							// optional default is GET
							con.setRequestMethod("GET");

							//add request header
							con.setRequestProperty("User-Agent", "Mozilla/5.0");

							int responseCode = con.getResponseCode();

							if(responseCode!=200)
							{
								Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
								showToast("Problem Connecting server, Please try again");
								
								
							}
							else
							{
								Log.i("Driverapp", "Server acepted free state"+responseCode);
								showToast("Successfully marked off duty");
								
							}
														}
						catch(Exception e)
						{
							Log.e("Driverapp", e.toString());

						}
					}
				};
				t1.start();

				x.remove("username");
				x.remove("password");
				x.commit();
			//	stopService((new Intent(getBaseContext(), Location_service.class)));
				stopService((new Intent(RegisterActivity.this, Location_service.class)));

				//send ms to server makind drvr busy

				Intent intent= new Intent(context,LoginActivity.class);
				startActivity(intent);


			}
		});
		//end logout button

		//onoff button
		Button btnOnOffDuty = (Button) findViewById(R.id.OnOffDuty);
	//	btnOnOffDuty.setText(Dutytext);

		btnOnOffDuty.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				//	String onoff;
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
	
				if(!spref.getBoolean("duty", false)) //if current value is false, driver wants to go on duty
				{
					Dutytext="Go Off Duty";
			        Button btnOnOffDuty = (Button) findViewById(R.id.OnOffDuty);
			        btnOnOffDuty.setText(Dutytext);
					onoff="free";
				        Log.e("test", "start service entered");
				        Editor e1=spref.edit();
				        e1.putBoolean("duty", true);
				        e1.putString("drvstat", "Go Off Duty");
				        e1.commit();
				        						
						String username= spref.getString("username","");
						String password=spref.getString("password", "");
		    		 
		    		 
	        		 String url="http://testapp1pranav.appspot.com/markdriver?email="+username+"&pass="+password+"&state=free";  

						Editor editor = spref.edit();
						editor.putString("url", url);
						editor.commit();

						

						Thread t1 = new Thread()
						{
							public void run()
							{

								//send to server
								try
								{
									SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

									String url = spref.getString("url", "");

									URL obj = new URL(url);
									HttpURLConnection con = (HttpURLConnection) obj.openConnection();

									// optional default is GET
									con.setRequestMethod("GET");

									//add request header
									con.setRequestProperty("User-Agent", "Mozilla/5.0");

									int responseCode = con.getResponseCode();

									if(responseCode!=200)
									{
										Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
										showToast("Problem Connecting server, Please try again");
										
										
									}
									else
									{
										Log.i("Driverapp", "Serevr acepted free state"+responseCode);
										showToast("Successfully marked free");
										
									}
																}
								catch(Exception e)
								{
									Log.e("Driverapp", e.toString());

								}
							}
						};
						t1.start();

/*				        Dutytext="Go Off Duty";
				        Button btnOnOffDuty = (Button) findViewById(R.id.OnOffDuty);
				        btnOnOffDuty.setText(Dutytext);*/
					    startService(new Intent(getBaseContext(), Location_service.class));					    	

				}

				else
				{
					Dutytext="Go on Duty";
			        Button btnOnOffDuty = (Button) findViewById(R.id.OnOffDuty);
			        btnOnOffDuty.setText(Dutytext);
					onoff="busy";
					Log.e("test", "stop service entered");
					Editor e1=spref.edit();
			        e1.putBoolean("duty", false);
			        e1.putString("drvstat", "Go on Duty");
			        e1.commit();
			        String username= spref.getString("username","");
					String password=spref.getString("password", "");
	    		 
	    		 
        		 String url="http://testapp1pranav.appspot.com/markdriver?email="+username+"&pass="+password+"&state=busy";  

					Editor editor = spref.edit();
					editor.putString("url", url);
					editor.commit();

					

					Thread t1 = new Thread()
					{
						public void run()
						{

							//send to server
							try
							{
								SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

								String url = spref.getString("url", "");

								URL obj = new URL(url);
								HttpURLConnection con = (HttpURLConnection) obj.openConnection();

								// optional default is GET
								con.setRequestMethod("GET");

								//add request header
								con.setRequestProperty("User-Agent", "Mozilla/5.0");

								int responseCode = con.getResponseCode();

								if(responseCode!=200)
								{
									Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
									showToast("Problem Connecting server, Please try again");
									
									
								}
								else
								{
									Log.i("Driverapp", "Serevr acepted free state"+responseCode);
									showToast("Successfully marked Off duty");
									
								}
															}
							catch(Exception e)
							{
								Log.e("Driverapp", e.toString());

							}
						}
					};
					t1.start();

					stopService((new Intent(getBaseContext(), Location_service.class)));	

				}				
			}
		});
	}
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}


	@Override
	public void onResume(){
	    super.onResume();
	    SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
		Button b=(Button)findViewById(R.id.OnOffDuty);
		b.setText(spref.getString("drvstat", "ERROR"));

		if(spref.getString("username", "XYZZY").equals("XYZZY")) //usr already logged in. show register activity
		{
			Intent a = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(a);
		}


	}
	}
