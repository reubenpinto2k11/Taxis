package com.example.taxiclient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


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
				sendNotification("Send error: " + extras.toString(),"");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString(),"");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				
			
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				
				
				String address="",city="";
				 try{
						
					 String[] valarr=extras.get(Config.MESSAGE_KEY).toString().split(",");
					 					 
					 valarr=new String[2];
						valarr[0]="15.3267";
						valarr[1]="73.9329";	
					 Geocoder geocoder;
				 List<Address> addresses;
				 geocoder = new Geocoder(this, Locale.getDefault());
				 addresses = geocoder.getFromLocation(Double.parseDouble(valarr[0]), Double.parseDouble(valarr[1]), 1);

				  address = addresses.get(0).getAddressLine(0);
				  city = addresses.get(0).getAddressLine(1);
				
				    }catch(IOException e){
				        e.printStackTrace();
				    }

				 //sendNotification("Your Customer is waiting at: "+ extras.get(Config.MESSAGE_KEY),extras.get(Config.MESSAGE_KEY).toString());
				 sendNotification("Your Driver is currently at: "
							+  address+", "+ city,extras.get(Config.MESSAGE_KEY).toString());
			
				
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg,String locationString) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		Intent callthis= new Intent(this,Second_client.class);
		callthis.putExtra("message",locationString);
		
		
		
		
		

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				callthis, 0);
		
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.btn_blue_matte)
				.setContentTitle("We found you a taxi.")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg)
				.setSound(soundUri)
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis());

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}
}
