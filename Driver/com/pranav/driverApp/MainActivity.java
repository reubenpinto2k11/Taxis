package com.pranav.driverApp;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pranav.driverApp.R;

public class MainActivity extends Activity {

	ShareExternalServer appUtil;
	String regId;
	AsyncTask<Void, Void, String> shareRegidTask;
	static String username="";
	static String password="";
	public static String customerid="";
	public static long servertime=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appUtil = new ShareExternalServer();
		SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
		username=spref.getString("username","CAN'T HAPPEN");
		password=spref.getString("password","CAN'T HAPPEN");



		String val =getIntent().getStringExtra("message");
		String srvrtime=getIntent().getStringExtra("servertime");


		String[] valarr;
		try
		{
			valarr = val.split(",");
		}
		catch(Exception e)
		{
			valarr =new String[]{"WRNG DATA","WRNG DATA"};

		}
		
		
		
		
		servertime=Long.parseLong(spref.getString("servertime", "0"));
		Editor x=spref.edit();
		x.remove("servertime");
		x.commit();
		
		
		String addr="";
		 Geocoder geocoder;
		 List<Address> addresses;
		 geocoder = new Geocoder(this, Locale.getDefault());
		 try {
			addresses = geocoder.getFromLocation(Double.parseDouble(spref.getString("lati","0")), Double.parseDouble(spref.getString("longi","0")), 1);
			 String address = addresses.get(0).getAddressLine(0);
			 String  city = addresses.get(0).getAddressLine(1);
			 addr=address+city;
			 
		 }
		 catch(Exception e)
		 {
			 
		 }
		 
		
		
		
		String displayText="The customer is at :"+ spref.getString("lati","0")+" latitude "+spref.getString("longi","0") +"Longitude \n"+addr+" and wants to go to  "+ spref.getString("dest","");
		
		Editor xx= spref.edit();
		xx.remove("dest");
	//	xx.putString("cust", valarr[2]);
		xx.commit();
		
		
		TextView t= new TextView(this);
		t=(TextView)findViewById(R.id.lblMessage);
		t.setText(displayText);



		Button navigate= (Button) findViewById(R.id.Navigate);
		navigate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String val =getIntent().getStringExtra("message");
				String valarr[] = val.split(",");
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

				String mLat=spref.getString("lati","0");
				String mLong=spref.getString("longi","0");

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +mLat+","+mLong));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);			}
		});

		navigate.setVisibility(View.GONE);
		navigate.setVisibility(View.INVISIBLE);


		Button buttonY = (Button) findViewById(R.id.buttonYes);

		buttonY.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				
            //check time
			//lat + "," + lon + "," + custEmail+","+destination+","+timenow;
				
				long timenow = new Date().getTime();
				if(timenow-servertime<60*2*1000)
				{
				
					SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

				//call server with yes url
				String custid=spref.getString("cust","ERROR");
				Editor a=spref.edit();
				//a.remove("cust");
				a.commit();
				
				String url="http://testapp1pranav.appspot.com/yesno?driver="+username+"&user="+custid+"&choice=yes";  
				
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

							if(responseCode==200)
							{Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
							showToast("Response conveyed to server");
							
							finish();
							}
						
						else
						{
							showToast("Could Not connect to server. Please try again");
						}
						
						}
						catch(Exception e)
						{
							Log.e("Driverapp", e.toString());

						}





					}
				};
				t1.start();


				

				//show navigate button
				//Button navigate= (Button) findViewById(R.id.Navigate);
				
				Toast.makeText(getBaseContext(), "Yes Clicked. Code to Dismiss activity",Toast.LENGTH_LONG).show();;

			//	navigate.setVisibility(View.VISIBLE);
				}
				
				else
				{
				Toast.makeText(getBaseContext(), "Sorry, The message has timed out.",Toast.LENGTH_LONG).show();
				finish();
				}
				
			}

		});



		Button buttonN = (Button) findViewById(R.id.buttonNo);

		buttonN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {


				
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

				String url="http://testapp1pranav.appspot.com/yseno?driver="+username+"&user="+	spref.getString("cust","ERROR")+"&choice=no";
//				
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

							if(responseCode==200)
								{Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
								showToast("Response conveyed to server");
								
								finish();
								}
							
							else
							{
								showToast("Could Not connect to server. Please try again");
							}
						
						}
						catch(Exception e)
						{
							Log.e("Driverapp", e.toString());

						}





					}
				};
				t1.start();




				//hide button just in case

				Button navigate= (Button) findViewById(R.id.Navigate);

				navigate.setVisibility(View.GONE);

				Toast.makeText(getBaseContext(), "No Clicked. Code to Dismiss activity",Toast.LENGTH_LONG).show();;
				


			}
		});



	}
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	

	@Override
	protected void onResume() 
	{
		super.onResume();
		Button navigate= (Button) findViewById(R.id.Navigate);

		navigate.setVisibility(View.VISIBLE);

	}

}
