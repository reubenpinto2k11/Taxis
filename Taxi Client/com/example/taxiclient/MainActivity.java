package com.example.taxiclient;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.view.Menu;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.StringBuffer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import android.os.Looper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener
{
	private Spinner spinner1;
	int flag;
	String taxis,dest;
	private Context context = this;
	EditText d1;
	//String dest;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

		if(spref.getString("username", "XYZZY").equals("XYZZY")) //usr already logged in. show register activity
		{
			Intent a = new Intent(MainActivity.this, Loginprompt.class);
			startActivity(a);
		}



		Button Logout = (Button) findViewById(R.id.buttonYes);

		Logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				Editor x= spref.edit();
				x.remove("username");
				x.commit();

				Intent intent= new Intent(MainActivity.this,Loginprompt.class);
				startActivity(intent);


			}
		});


		spinner1 = (Spinner) findViewById(R.id.spinner1);
		
	       final List<String> inf = new ArrayList<String>();
	       inf.add("Select a Taxi");
	       inf.add("Any");
	       Thread t1 = new Thread()
	       {
	       	public void run()
	       	{
	       		Looper.prepare();
	       		try
	       		{
	       			String address="http://testapp1pranav.appspot.com/gettaxi";
	       			URL object = new URL(address);
	 			    HttpURLConnection connect = (HttpURLConnection) object.openConnection();
	 			    connect.setRequestMethod("GET");
	 			    connect.connect();
	 			    BufferedReader reader = new BufferedReader( new InputStreamReader(connect.getInputStream()));
	 			    String line;    			
	 			    while ((line = reader.readLine()) != null)
	 			    {
	 			    	inf.add(line);
	 			    }
	 			    reader.close();
	       		}
	     		catch(Exception e)
	     		{
	     			Log.d("taxiclient",e.toString());
	     		}
	     		Looper.loop();
	     	}

	     };//end of thread
	     
	     t1.start();
	      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,inf); 
	      dataAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
	      spinner1.setAdapter(dataAdapter);
	     spinner1.setOnItemSelectedListener(new OnItemSelectedListener() 
	     {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int pos,long id)
			{
				// TODO Auto-generated method stub
				if(parent.getItemAtPosition(pos).toString()=="Select a Taxi")
				{
					flag=1;
				}
				else
				{
					taxis= (String)spinner1.getAdapter().getItem(pos);
					SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
					Editor x= spref.edit();
					x.putString("taxis",taxis);
					x.commit();
				
				Toast.makeText(parent.getContext(),"you selected:\n"+taxis ,Toast.LENGTH_SHORT).show();
				  flag=0;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
			}
		 
	     });
	    

		LocationService l = new  LocationService(MainActivity.this);
		if(l.canGetLocation())
		{
			final String lat=Double.toString(l.getlat());
			final String lon=Double.toString(l.getlon());

			Toast.makeText(getApplicationContext(), "Latitude:"+lat+"\n"+"Longitude:"+lon, Toast.LENGTH_LONG).show();
			 Editor editor = spref.edit();
		      editor.putString("lat", lat);
		      editor.putString("lon", lon);
		      editor.commit();


	 
				
			final String id = spref.getString("username", "");

			//
			Button b1 = (Button)findViewById(R.id.button1);
			b1.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0) {
					SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
					 d1 = (EditText)findViewById(R.id.editText1);
					 String dest = d1.getText().toString();
					 Spinner spin=(Spinner)findViewById(R.id.spinner1);
					 
					 Editor editor = spref.edit();
					 	
                     editor.putString("taxis",spin.getSelectedItem().toString()); 
				      editor.putString("destination", dest);
				      editor.commit();

				      
					if(flag!=1)
					{
						if(d1.getText().length()== 0)
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
							alertDialogBuilder.setTitle("");
							alertDialogBuilder
								.setMessage("Enter destination!")
								.setCancelable(false)
								.setPositiveButton("OK",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) 
									{
										//do nothing
									}
								  });
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
						}
						else
						{
								Thread t = new Thread()
								{
						//public Handler mHandler;        
						public void run()
						{
							// TODO Auto-generated method stub

							Looper.prepare(); //For Preparing Message Pool for the child Thread
							String info;
							try
							{
								SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
								String type=spref.getString("taxis", "Empty");
                                String dest=spref.getString("destination","EMPTY");
								String url="http://testapp1pranav.appspot.com/devserver?id="+id+"&lat="+lat+"&lon="+lon+"&type="+type+"&destination="+dest;
								Log.w("taxiapp","Url is:"+url );
								URL obj = new URL(url);
								HttpURLConnection con = (HttpURLConnection) obj.openConnection();

								// optional default is GET
								con.setRequestMethod("GET");

								//add request header
								con.setRequestProperty("User-Agent", "Mozilla/5.0");

								int responseCode = con.getResponseCode();

								System.out.println("Response Code : " + responseCode);

								BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
								String input;
								StringBuffer buff = new StringBuffer();

								while ((input = in.readLine()) != null)
								{
									buff.append(input);
								}
								in.close();

								info =buff.toString();
								Log.w("taxiapp","response is:"+info );
								
								
								if(responseCode==200)
								{
									Intent i = new Intent(MainActivity.this,TaxiFound.class);
									startActivity(i);
								}

							}

							catch (UnknownHostException h)
							{
								Toast.makeText(getApplicationContext(),"Internet connection not available", Toast.LENGTH_LONG).show(); 

							}
							catch(Exception e)
							{
								e.printStackTrace();
							}

							/* mHandler = new Handler()
   			                {              
   			                public void handleMessage(Message msg) 
   			               {                   // process incoming messages here              
   			                }        
   			                  }; */         

							Looper.loop(); //process messages till loop stopped
						}//end run

					};//end of thread
					t.start();
				}//end of inner else
					
					}//end of outer if
			else
					{
						
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
						alertDialogBuilder.setTitle("");
						alertDialogBuilder
							.setMessage("Please select Taxi type")
							.setCancelable(false)
							.setPositiveButton("OK",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) 
								{
									//do nothing
								}
							  });
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

				}
				}
			});//end of button

		}//end of if
		else
		{
			Toast.makeText(getApplicationContext(), "location not found", Toast.LENGTH_LONG).show();
		}



	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,long arg3) 
	{
		// TODO Auto-generated method stub
		taxis=parent.getItemAtPosition(pos).toString();
		//Toast.makeText(parent.getContext(), "On Item Select : \n" + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
		Toast.makeText(parent.getContext(), "you have selected : \n" + taxis, Toast.LENGTH_LONG).show();

	}

	public void onNothingSelected(AdapterView<?> arg0) 
	{
		// TODO Auto-generated method stub

	}


	@SuppressLint("NewApi")
	public void createNotification() {
		// Prepare intent which is triggered if the
		// notification is selected
		Intent intent = new Intent(this, TaxiFound.class);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


		String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 15.4989, 73.8278);
		Intent mapintent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapintent, 0);



		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(this)
		.setContentTitle("You Just found a taxi")
		.setContentText("We have Located a taxt for you. The taxi will arrive in"+ 10 + "Minutes").setSmallIcon(R.drawable.btn_green_matte)
		.setContentIntent(pIntent)

		.addAction(R.drawable.btn_blue_matte, "How Long?", pIntent)
		.addAction(R.drawable.btn_blue_matte, "Map", mapPendingIntent)
		.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti);

	}




}

//ServerReply class for matching strings from severs reply
class ServerReply
{

	public String id,lat,lon,distance;
	//show content

}


