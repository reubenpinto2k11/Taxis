package com.pranav.driverApp;

import java.net.HttpURLConnection;
import java.net.URL;



import android.app.Activity;
//import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
//import android.os.Build;
import android.widget.Toast;

public class CustResponse extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cust_response);
		
		Button bk=(Button)findViewById(R.id.bckbutton);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//get message and perform switch based on that
	    String yesno1 =getIntent().getStringExtra("message");
		
		 SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
		 String yesno=spref.getString("choicess", "");
		 Editor x=spref.edit();
		 x.remove("choicess");
		 x.commit();
		
		
		TextView messagetxtview = (TextView) findViewById(R.id.Detailedmsg);
	   if(yesno.equals("yes"))
	   {
		   //show text enable button
	     messagetxtview.setText("The customer has accepted the ride.");
	     Button navigate= (Button) findViewById(R.id.navigatebtn);
	     
	     navigate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {

					//get loaction from sharedpref
					
					
					 SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
					 String lat=spref.getString("lat", "0.0");
					 String lon=spref.getString("lon", "0.0");

					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +lat+","+lon));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);			}
			});
	    
	     navigate.setVisibility(View.VISIBLE);
	   }
	   
	   else if(yesno.equals("no"))
	   {
		     messagetxtview.setText("The did not agree for the ride.");

		     Button navigate= (Button) findViewById(R.id.navigatebtn);
		     navigate.setVisibility(View.INVISIBLE);
		     
	   }
	   
	   bk.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			finish();			
		}
	});
	   
	   
	     Button back= (Button) findViewById(R.id.okaybtn);
	     back.setOnClickListener(new View.OnClickListener() {
	    	 public void onClick(View arg0) {

	    		 //mark driver as free using markdriver
					SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

					
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
    	 
	    	 }
			});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cust_response, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(CustResponse.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_cust_response,
					container, false);
			return rootView;
		}
	}

}
