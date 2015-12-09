package com.pranavtalking.devserver;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;

import com.google.android.gcm.server.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class YesNo extends HttpServlet{
	
	private static final String GOOGLE_SERVER_KEY = "AIzaSyDef5-US07A95ckj030mwMboJqVwWCQH74";
	public static final String MESSAGE_KEY = "TestMessage";
    
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String driverid=request.getParameter("driver");
		String Custid=request.getParameter("user");
		String choice=request.getParameter("choice");
		
		if(choice.toLowerCase().equals("yes"))
		{
			//delete from table, send a gcm message to customer
			
			try
			{
			  response.setContentType("text/plain");
			  response.getWriter().println("entered yes \n");
			}
			catch(Exception e)
			{
			  Log.warn(e.toString());
			}
			
			
			Filter cust = new FilterPredicate("cust_email", FilterOperator.EQUAL,Custid);
			   Query q = new Query("chosenlist").setFilter(cust);
			   PreparedQuery pq = datastore.prepare(q);
		
				
				try
				{
				  response.setContentType("text/plain");
				  response.getWriter().println("found customer "+Custid +"driver saying yes is "+ driverid);
				}
				catch(Exception e)
				{
				  Log.warn(e.toString());
				}
				
				
				
			
				
				
			   Entity r= pq.asSingleEntity();
			   
				if (r!=null) {
					String drvr = "";
					String lat = "", lon = "";
					int chosen = Integer.parseInt(r.getProperty("chosen")
							.toString());
					
					if(chosen==0)
					{
						try
						{
							String x=r.getProperty("Driver1Lat").toString();
						}
						catch(Exception e)
						{
						chosen=3;
						}
					}
					
					if(chosen==1 )
					{
						try
						{
							String x=r.getProperty("Driver2Lat").toString();
						}
						catch(Exception e)
						{
						chosen=3;
						}
					}
					if(chosen==2 )
					{
						try
						{
							String x=r.getProperty("Driver3Lat").toString();
						}
						catch(Exception e)
						{
						chosen=3;
						}
					}
					
					//chosen--;
					switch (chosen) {
					case 0:
						drvr = r.getProperty("Driver1").toString();
						lat = r.getProperty("Driver1Lat").toString();
						lon = r.getProperty("Driver1Long").toString();
						Log.warn("got from r:"+lat);

						break;

					case 1:
						drvr = r.getProperty("Driver2").toString();
						lat = r.getProperty("Driver2Lat").toString();
						lon = r.getProperty("Driver2Long").toString();
						break;
					case 2:
						drvr = r.getProperty("Driver3").toString();
						lat = r.getProperty("Drive3Lat").toString();
						lon = r.getProperty("Driver3Long").toString();
						break;
					}
					datastore.delete(r.getKey());
					
					
		
					//store cust data and driver data in a seperate repliedyes object
					
					
					Entity match= new Entity("matched",Custid); //kind, key
				    match.setProperty("cust_email", Custid);
				    match.setProperty("driver_email", drvr);
					datastore.put(match);
						   
					
					
					//tell customer about driver
					Filter Custdata = new FilterPredicate("email",
							FilterOperator.EQUAL, Custid);
					Query q1 = new Query("user").setFilter(Custdata);
					PreparedQuery pq1 = datastore.prepare(q1);
					String regId = "";
					@SuppressWarnings("deprecation")
					int count = pq1.countEntities();
					if (count == 0) {
						//Cannot HAPPEN!!!!!!! CHeck code

					} else {
						Entity res = pq1.asSingleEntity();
						regId = res.getProperty("gcmid").toString();

					}
					final Logger log = Logger.getLogger(DevserverServlet.class
							.getName());
					log.warning("got info from datastore");
					String userMessage = lat + "," + lon;
					Sender sender = new Sender(GOOGLE_SERVER_KEY);
					
					try
					{
					  response.setContentType("text/plain");
					  response.getWriter().println("Sent request to"+regId+" Custid="+Custid+" message="+userMessage );
						log.warning(userMessage);

					}

					catch(Exception e)
					{
					  Log.warn(e.toString());
					}
					
					
					Message message = new Message.Builder().timeToLive(120)
					//.delayWhileIdle(true)
							.addData(MESSAGE_KEY, userMessage).build();
					Result result = null;
					try {
						result = sender.send(message, regId, 2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//request.setAttribute("pushStatus", result.toString());
					log.warning("sent gcm message " + result.toString());
				}
				else
				{
					final Logger log = Logger.getLogger(DevserverServlet.class
							.getName());
					
					log.warning("Wrong username");
				}
			   
			
		}
		
		else if(choice.toLowerCase().equals("no"))
		{
			//choose the next driver. 
			//increment counter, call cronjob
			

			Filter cust = new FilterPredicate("Cust_Email", FilterOperator.EQUAL,Custid);
			   Query q = new Query("chosenlist").setFilter(cust);
			   PreparedQuery pq = datastore.prepare(q);
			   
			   Entity r= pq.asSingleEntity();
			   
			   if (r!=null) {
				r.setProperty(
						"chosen",
						Integer.parseInt(r.getProperty("chosen").toString()) + 1);
				datastore.put(r);
				Cronjob x = new Cronjob();
				x.doGet(request, response);
			}
			   
			   
				else
				{
					final Logger log = Logger.getLogger(DevserverServlet.class
							.getName());
					
					log.warning("Wrong username");
				}

		}
		
		
		
		

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		doGet(request, response);
	}
}
