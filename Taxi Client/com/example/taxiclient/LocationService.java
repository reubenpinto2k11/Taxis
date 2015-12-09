package com.example.taxiclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
public class LocationService extends Service implements LocationListener
{
	double lat,lon;
	Location loc;
	boolean gps=false,net=false;
	boolean cangetloc=false;
	protected LocationManager locationmanager;
	final long MINd = 10; // 10 meters
    // The minimum time between updates in milliseconds
    final long MINt = 1000 * 60 * 1; // 1 minute
	private Context c;
    
	public LocationService(Context x)//constructor 
	{
		// TODO Auto-generated constructor stub
		c=x;
		 loca();
	}
	protected  Location loca()
	{
	 
	try
		{	
		locationmanager =(LocationManager)c.getSystemService(LOCATION_SERVICE);
		
			gps = locationmanager.isProviderEnabled(locationmanager.GPS_PROVIDER);
			net = locationmanager.isProviderEnabled(locationmanager.NETWORK_PROVIDER);
			if((!gps)&&(!net))
			{
				
			}
			else
			{
				this.cangetloc=true;
				
				if(net)
				{
					locationmanager.requestLocationUpdates(locationmanager.NETWORK_PROVIDER, MINt,MINd, this);
					Log.d("Network", "Network"); 
					if(locationmanager!=null)
					 {
						 loc = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						 if (loc!= null) 
						 {
	                         lat = loc.getLatitude();
	                         lon = loc.getLongitude();
	                     }

					 }
					
	            }
	            
	            if (gps)
	            {
	                if (loc == null)
	                {  
	                	locationmanager.requestLocationUpdates(locationmanager.GPS_PROVIDER, MINt,MINd, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationmanager != null) 
                    {
                        loc = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) 
                        {
                            lat = loc.getLatitude();
                            lon = loc.getLongitude();
                        }
                    }
	                }
	            }
	        }

	    } 
		catch (Exception e)
		{
	        e.printStackTrace();
	    }

	    return loc;
	}
//auto generated functions
	public void onLocationChanged(Location location)
	{
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
	    return null;
	}
	/////
	public double getlat(){
	    if(loc != null){
	        lat = loc.getLatitude();
	    }
	     
	    // return latitude
	    return lat;
	}
	public double getlon()
	{
	    if(loc != null)
	    {
	        lon = loc.getLongitude();
	    }
	     
	    // return longitude
	    return lon;
	}
	public boolean canGetLocation()
	{
	    return this.cangetloc;
	}



}
