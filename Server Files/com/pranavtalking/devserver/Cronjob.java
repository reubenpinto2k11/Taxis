package com.pranavtalking.devserver;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
//import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;
import com.google.android.gcm.server.*;
//import com.sun.org.apache.regexp.internal.RESyntaxException;

@SuppressWarnings("serial")
public class Cronjob extends HttpServlet {

	private static final String GOOGLE_SERVER_KEY = "AIzaSyDef5-US07A95ckj030mwMboJqVwWCQH74";
	static final String MESSAGE_KEY = "TestMessage";
	private static final Logger log = Logger.getLogger(DevserverServlet.class
			.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		// iterate through the entire table, save each result back
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query q = new Query("chosenlist");
		PreparedQuery pq = datastore.prepare(q);

		
		for (Entity result : pq.asIterable()) {

			
			String custEmail = (String) result.getProperty("cust_email");
			String destination = (String) result.getProperty("destination");
			String wtf=String.valueOf( result.getProperty("chosen"));
			int chosen = Integer.valueOf(wtf);
//			long time = Long.valueOf((String) result.getProperty("time"));
		//	long time=Long.parseLong((String) result.getProperty("time"));
			long time=(long)Long.parseLong( result.getProperty("time").toString());

			long CurrentTime = new Date().getTime();

			if ((CurrentTime - time) / 1000 >240) // been more then 4 minutes
			{
				chosen++;
				result.setProperty("chosen", chosen);
				result.setProperty("time", new Date().getTime());
				datastore.put(result);
				
				
				
				if(chosen==1)
				{ 
					try
					{
					String hack=(String) result.getProperty("Driver2");
					if(hack.isEmpty())
					{
						chosen=3;
					}
					}
					catch(Exception e)
					{
						chosen=3;
					}
				}
				
				
				

				if(chosen==2)
				{ //wtf code ahead
					try
					{
					String hack=(String) result.getProperty("Driver3");
					if(hack.isEmpty())
					{
						chosen=3;
					}
					}
					catch(Exception e)
					{
						chosen=3;
					}
				}
				
				
				if (chosen == 3) {
					// delete result from db and notify customer
					datastore.delete(result.getKey());
					
					//send gcm to user

					   Filter Custdata= new  FilterPredicate("email", FilterOperator.EQUAL,custEmail);
						Query q1 = new Query("user").setFilter(Custdata);

						PreparedQuery pq1 = datastore.prepare(q1);
						
					  String regId="";
					   @SuppressWarnings("deprecation")
					int count=pq1.countEntities();
					   if(count==0)
					   {
						 						  
					   }
					   else
					   {
						   Entity res=pq1.asSingleEntity();
						   regId=res.getProperty("gcmid").toString();
						   
						   
					   }
					   log.warning("got info from datastore" ); 
					   
						String userMessage = "SORRY: No Taxi Could be found for your location";
						Sender sender = new Sender(GOOGLE_SERVER_KEY);
						Message message = new Message.Builder().timeToLive(120)
								//.delayWhileIdle(true)
								.addData(MESSAGE_KEY, userMessage).build();
						
						@SuppressWarnings("unused")
						Result resultx = null;
						try {
							resultx = sender.send(message, regId, 2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//request.setAttribute("pushStatus", result.toString());
						log.warning("sent gcm message "+result.toString());
						
				}

			}

		//	else {
				// send gcm request to driver number x
				// chosen++ if 3, delete entire row
			
			
			
			
			
			
			
			
			if(chosen==1)
			{ 
				try
				{
				String h=(String) result.getProperty("Driver2");
				if(h.isEmpty())
				{
					chosen=3;
				}
				}
				catch(Exception e)
				{
					chosen=3;
				}
			}
			
			
			

			if(chosen==2)
			{ 
				try
				{
				String h=(String) result.getProperty("Driver3");
				if(h.isEmpty())
				{
					chosen=3;
				}
				}
				catch(Exception e)
				{
					chosen=3;
				}
			}
			
			
			if (chosen == 3) {
				// delete result from db and notify customer
				datastore.delete(result.getKey());
				
				//send gcm to user

				   Filter Custdata= new  FilterPredicate("email", FilterOperator.EQUAL,custEmail);
					Query q1 = new Query("user").setFilter(Custdata);

					PreparedQuery pq1 = datastore.prepare(q1);
					
				  String regId="";
				   @SuppressWarnings("deprecation")
				int count=pq1.countEntities();
				   if(count==0)
				   {
					 					  
				   }
				   else
				   {
					   Entity res=pq1.asSingleEntity();
					   regId=res.getProperty("gcmid").toString();
					   
					   
				   }
				   log.warning("got info from datastore" ); 
				   
					String userMessage = "SORRY: No Taxi Could be found for your location";
					Sender sender = new Sender(GOOGLE_SERVER_KEY);
					Message message = new Message.Builder().timeToLive(120)
							//.delayWhileIdle(true)
							.addData(MESSAGE_KEY, userMessage).build();
					
					@SuppressWarnings("unused")
					Result resultx = null;
					try {
						resultx = sender.send(message, regId, 2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//request.setAttribute("pushStatus", result.toString());
					log.warning("sent gcm message "+result.toString());
					
			}

			
			
			
			
			

				String drvr = "";
				String lat = "", lon = "";

				switch (chosen) {
				case 0:
					drvr = result.getProperty("Driver1").toString();
					lat = result.getProperty("Driver1Lat").toString();
					lon = result.getProperty("Driver1Long").toString();
					break;

				case 1:
					drvr = result.getProperty("Driver2").toString();
					lat = result.getProperty("Driver2Lat").toString();
					lon = result.getProperty("Driver2Long").toString();
					break;
				case 2:
					drvr = result.getProperty("Driver3").toString();
					lat = result.getProperty("Drive3Lat").toString();
					lon = result.getProperty("Driver3Long").toString();
					break;
				}
				
				if(chosen<=2){

				Filter driverdata = new FilterPredicate("email",
						FilterOperator.EQUAL, drvr);
				Query q1 = new Query("driver").setFilter(driverdata);

				PreparedQuery pq1 = datastore.prepare(q1);

				String regId = "";
				@SuppressWarnings("deprecation")
				int count = pq1.countEntities();
				if (count == 0) {
					

				} else {
					Entity res = pq1.asSingleEntity();
					regId = res.getProperty("gcmid").toString();

				}
				log.info("got info from datastore from driver table Drvr is "
						+ drvr);
				long timenow = new Date().getTime();
				String userMessage = lat + "," + lon + "," + custEmail+","+destination+","+timenow;
				log.warning("Time is "+timenow+"   \n sent msg to user "+ userMessage);
				
				for(int i=0;i<3;i++)
				{
					   log.warning("x" ); 
				}
				Sender sender = new Sender(GOOGLE_SERVER_KEY);
				Message message = new Message.Builder().timeToLive(120)
				// .delayWhileIdle(true)
						.addData(MESSAGE_KEY, userMessage).build();

				@SuppressWarnings("unused")
				Result resultx = null;

				try {
					resultx = sender.send(message, regId, 2);

					chosen++;
					result.setProperty("chosen", chosen);
					result.setProperty("time", new Date().getTime());
					datastore.put(result);

					// request.setAttribute("pushStatus", result.toString());
					log.info("sent gcm message " + result.toString());

				} catch (Exception e) {
					log.severe("ERROR" + e.toString());
			}
			
				//check if driver is inactive for 10 minutes, if yes mark him as off duty
				
			
			}

		}

	}

}
