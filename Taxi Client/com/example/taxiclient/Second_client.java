package com.example.taxiclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;





import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
public class Second_client extends Activity {
String a,b,c,d;
TextView textvieweta;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_client);
		Intent i=getIntent();
		Bundle bu=i.getExtras();
		 TextView eta=(TextView)findViewById(R.id.textVieweta);
		 Button b1= (Button) findViewById(R.id.button1);
		 b1.setVisibility(View.GONE);

		
		String val =getIntent().getStringExtra("message");


		String[] valarr;
		try
		{
			valarr = val.split(",");
		}
		catch(Exception e)
		{
			valarr =new String[]{"WRNG DATA","WRNG DATA","WRNG DATA"};
		}
		valarr=new String[2];
		
		
		valarr[0]="15.3267902";
		
		valarr[1]="73.9329389";		
		b=valarr[0]; //lat
		c=valarr[1]; //long
		
		
		
		
		 Geocoder geocoder;
		 List<Address> addresses;
		 geocoder = new Geocoder(this, Locale.getDefault());
		 try {
			addresses = geocoder.getFromLocation(Double.parseDouble(valarr[0]), Double.parseDouble(valarr[1]), 1);
			 String address = addresses.get(0).getAddressLine(0);
			 String  city = addresses.get(0).getAddressLine(1);
			 d= address+", "+city;
			 
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		  
		  
		
		TextView t6=(TextView)findViewById(R.id.textView6);
		TextView t7=(TextView)findViewById(R.id.textView7);
		TextView t8=(TextView)findViewById(R.id.textView8);
		TextView t9=(TextView)findViewById(R.id.textView9);
		t6.setText(a);
		t7.setText(b);
		t8.setText(c);
		t9.setText(d);
		
		
		
		//get request lat and long values from sharedpref
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				double yourlat=Double.valueOf(spref.getString("lat","0.00"));
				double yourlon=Double.valueOf(spref.getString("lon","0.00"));
				double driverlat=Double.valueOf(valarr[0]);
				double driverlon=Double.valueOf(valarr[1]);
				  LatLng origin =new LatLng(driverlat,driverlon);
                  LatLng dest = new LatLng(yourlat, yourlon);
				
                  String url = getDirectionsUrl(origin, dest);
                 DownloadTask downloadTask = new DownloadTask();
                   // Start downloading json data from Google Directions API
                   downloadTask.execute(url);

		
		
		Button buttonY = (Button) findViewById(R.id.buttonYes);

		buttonY.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				//call server with yes url
				
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				String username=spref.getString("username","CAN'T HAPPEN");


				String url="http://testapp1pranav.appspot.com/yesnocust?user="+username+"&choice=yes";  

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
							if(responseCode==200)
							{
								showToast("Message sent to the driver. The driver will arrive shortly");
								Button ys=(Button)findViewById(R.id.buttonYes);
								ys.setVisibility(View.INVISIBLE);
								Button nn=(Button)findViewById(R.id.button2);
								nn.setVisibility(View.INVISIBLE);
							}
	
						}
						catch(Exception e)
						{
							Log.e("Driverapp", e.toString());

						}





					}
				};
				t1.start();


				
				Toast.makeText(getBaseContext(), "Yes Clicked.Booking Taxi.....",Toast.LENGTH_LONG).show();;
				
				
			}

		});



		
		
		
		Button buttonN=(Button)findViewById(R.id.button2);

		buttonN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				//call server with yes url
				
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				String username=spref.getString("username","CAN'T HAPPEN");


				String url="http://testapp1pranav.appspot.com/yesnocust?user="+username+"&choice=no";  

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
							{	Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
							showToast("Problem Contacting server, Please try again");
							}
							else
							{
								showToast("We could not find any better taxi for you at the moment. Please try again after some time");
								finish();
							}
						
						}
						catch(Exception e)
						{
							Log.e("Driverapp", e.toString());

						}

					}
				};
				t1.start();


				
		//		Toast.makeText(getBaseContext(), "Yes Clicked.Booking Taxi.....",Toast.LENGTH_LONG).show();;
				
				
			}

		});


		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second_client, menu);
	
		return true;
	}
	
	
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(Second_client.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	
	
	
	
	
	
	
	
	
	
	 private String getDirectionsUrl(LatLng origin,LatLng dest){
		 
	        // Origin of route
	        String str_origin = "origin="+origin.latitude+","+origin.longitude;
	 
	        // Destination of route
	        String str_dest = "destination="+dest.latitude+","+dest.longitude;
	 
	        // Sensor enabled
	        String sensor = "sensor=false";
	 
	        // Building the parameters to the web service
	        String parameters = str_origin+"&"+str_dest+"&"+sensor;
	 
	        // Output format
	        String output = "json";
	 
	        // Building the url to the web service
	        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
	 
	        return url;
	    }
	 
	    /** A method to download json data from url */
	    private String downloadUrl(String strUrl) throws IOException{
	        String data = "";
	        InputStream iStream = null;
	        HttpURLConnection urlConnection = null;
	        try{
	            URL url = new URL(strUrl);
	 
	            // Creating an http connection to communicate with url
	            urlConnection = (HttpURLConnection) url.openConnection();
	 
	            // Connecting to url
	            urlConnection.connect();
	 
	            // Reading data from url
	            iStream = urlConnection.getInputStream();
	 
	            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
	 
	            StringBuffer sb  = new StringBuffer();
	 
	            String line = "";
	            while( ( line = br.readLine())  != null){
	                sb.append(line);
	            }
	 
	            data = sb.toString();
	 
	            br.close();
	 
	        }catch(Exception e){
	            Log.d("Exception while downloading url", e.toString());
	        }finally{
	            iStream.close();
	            urlConnection.disconnect();
	        }
	        return data;
	    }
	 
	    
	   // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                
				SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				Editor x= spref.edit();
				x.putString("data", data);
				x.commit();
                
                
                
                Thread t1 = new Thread()
				{
					public void run()
					{

						//send to server
						try
						{
							 JSONObject jObject;
							 
					            List<List<HashMap<String, String>>> routes = null;
					 
					            try{
									SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
									String data=spref.getString("data", "");

					                jObject = new JSONObject(data);
					                DirectionsJSONParser parser = new DirectionsJSONParser();
					 
					                // Starts parsing data
					                routes = parser.parse(jObject);
					                List<List<HashMap<String, String>>> result= routes;
					                
					                
					                
					                ArrayList<LatLng> points = null;
					                PolylineOptions lineOptions = null;
					                MarkerOptions markerOptions = new MarkerOptions();
					                String distance = "";
					                String duration = "";
					     
					                if(result.size()<1){
					                    Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
					                    return;
					                }
					     
					                // Traversing through all the routes
					                for(int i=0;i<result.size();i++){
					                    points = new ArrayList<LatLng>();
					                    lineOptions = new PolylineOptions();
					     
					                    // Fetching i-th route
					                    List<HashMap<String, String>> path = result.get(i);
					     
					                    // Fetching all the points in i-th route
					                    for(int j=0;j<path.size();j++){
					                        HashMap<String,String> point = path.get(j);
					     
					                        if(j==0){    // Get distance from the list
					                            distance = (String)point.get("distance");
					                            continue;
					                        }else if(j==1){ // Get duration from the list
					                            duration = (String)point.get("duration");
					                            continue;
					                        }
					     
					                        double lat = Double.parseDouble(point.get("lat"));
					                        double lng = Double.parseDouble(point.get("lng"));
					                        LatLng position = new LatLng(lat, lng);
					     
					                        points.add(position);
					                    }
					     
					                    // Adding all the points in the route to LineOptions
					                    lineOptions.addAll(points);
					                    lineOptions.width(2);
					                    lineOptions.color(Color.RED);
					                }
					                
					                textvieweta.setText(""+duration);

					                
					                
					            }
					            catch(Exception e){
					                e.printStackTrace();
					            }
							
							
						}
						catch(Exception e)
						{
							
						}
					}
				};
				
				t1.run();
				
				
						
                
                
                
                ParserTask parserTask = new ParserTask();
                parserTask.execute(data);
                
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return null;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            //ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            //parserTask.execute(result);
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
                onPostExecute(routes);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
 
            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
 
           
            
            textvieweta.setText(""+duration);
 
            // Drawing polyline in the Google Map for the i-th route
            //map.addPolyline(lineOptions);
        }
    }
 
	
}
