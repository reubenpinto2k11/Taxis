package com.pranav.driverApp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pranav.driverApp.R;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(),"","");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString(),"","");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				
		
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				
				
				String address="",city="";
				 try{
					 
					 //valarr stores msg received in csv strings
						
					 String[] valarr=extras.get(Config.MESSAGE_KEY).toString().split(",");
					 
					 //if message format is lat + "," + lon + "," + custEmail;
					 //check if valarr[0] is a number
					 if(isNumeric(valarr[0]))
					 {					 
					 Geocoder geocoder;
					 List<Address> addresses;
					 geocoder = new Geocoder(this, Locale.getDefault());
					 addresses = geocoder.getFromLocation(Double.parseDouble(valarr[0]), Double.parseDouble(valarr[1]), 1);

					 address = addresses.get(0).getAddressLine(0);
					 city = addresses.get(0).getAddressLine(1);
				  
					 //sendNotification("Your Customer is waiting at: "+ extras.get(Config.MESSAGE_KEY),extras.get(Config.MESSAGE_KEY).toString());
					 sendNotification("You have a customer waiting at: "
								+  address+", "+ city+ " And wishes to go to  "+valarr[3],extras.get(Config.MESSAGE_KEY).toString(),"request");
					 
					 //store location in sharedpref
					 SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

						Editor editor = spref.edit();
						editor.putString("lat", valarr[0]);
						editor.putString("lon", valarr[1]);
						editor.commit();

					 
					 }
					 
					 
					 //yes message
					 //message is of the form Custid, Y
					 else if(valarr[1].equals("Y")){
						 
						 sendNotification("The customer has agreed for the ride","","yes");
						 
						 //create notification
						 
						 
						 
						 //call activity to tell user said yes and show stored location 
						 
					 }
					 
					 
					 //no message
					//message is of the form Custid, N
					 else if(valarr[1].equals("N")){

						 sendNotification("The customer did not agree for the ride","","no");
						 
						 
					 }
				    }catch(IOException e){
				        e.printStackTrace();
				    }

				
				
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	
	
	
	
	
	private void sendNotification(String msg,String locationString,String type) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		
		if(type.equals("yes"))
		{
			
			
			Intent callthis= new Intent(this,CustResponse.class);
			callthis.putExtra("message","yes");
			SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
			Editor x= spref.edit();
			x.putString("choicess", "yes");
			x.commit();
						
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					callthis, 0);
			
			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.gcm_cloud)
					.setContentTitle("The Customer Agreed")
					.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
					.setContentText(msg)
					.setSound(soundUri)
				//	.setAutoCancel(true)
					.setWhen(System.currentTimeMillis());

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			Log.d(TAG, "Notification sent successfully.");
			
			
		}
		
		else if(type.equals("no"))
		{
			SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
			Editor x= spref.edit();
			x.putString("choicess", "no");
			x.commit();
			Intent callthis= new Intent(this,CustResponse.class);
			callthis.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			callthis.putExtra("message","no");
						
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,callthis, 0);
			
			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.gcm_cloud)
					.setContentTitle("The customer did not agree")
					.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
					.setContentText(msg)
					//.setAutoCancel(true)
					.setSound(soundUri)
					.setWhen(System.currentTimeMillis());

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			Log.d(TAG, "Notification sent successfully.");
			
		}
		
		else{
		
		Intent callthis= new Intent(this,MainActivity.class);
		callthis.putExtra("message",locationString);
		String arr[]=locationString.split(",");
		SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

		Editor editor = spref.edit();
		editor.remove("servertime");
		editor.remove("cust");
		editor.remove("lati");
		editor.remove("longi");
		editor.remove("dest");
		editor.commit();
		editor.putString("servertime", arr[4]);
		editor.putString("cust", arr[2]);
		editor.putString("lati", arr[0]);
		editor.putString("longi", arr[1]);
		editor.putString("dest", arr[3]);
		editor.commit();
		
		
		
		
	//	callthis.putExtra("servertime", arr[4]+"");
		
		
		
		
		

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				callthis, 0);
		
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.gcm_cloud)
				.setContentTitle("You Have a new fare")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg)
				.setAutoCancel(true)
				.setSound(soundUri)
				.setWhen(System.currentTimeMillis());

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
		}
	}
	
	
	
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    @SuppressWarnings("unused")
		double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
}
