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


//customer receives driver location. if yes, send driver a message if no also send a message. in case of no, Customer gets a tough luck message

@SuppressWarnings("serial")
public class YesNoCust extends HttpServlet{
	
	private static final String GOOGLE_SERVER_KEY = "AIzaSyDef5-US07A95ckj030mwMboJqVwWCQH74";
	public static final String MESSAGE_KEY = "TestMessage";
	
	private static final Logger log = Logger.getLogger(DevserverServlet.class
			.getName());
    
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String Custid=request.getParameter("user");
		String choice=request.getParameter("choice");
		
		if(choice.toLowerCase().equals("yes"))
		{
			//delete from table, send a gcm message to driver
			
			
			
			Filter cust = new FilterPredicate("cust_email", FilterOperator.EQUAL,Custid);
			   Query q = new Query("matched").setFilter(cust);
			   PreparedQuery pq = datastore.prepare(q);
		
				
				
				//get driver email
			   Entity r= pq.asSingleEntity();
			   String driver="";
				if (r!=null) {
					driver=r.getProperty("driver_email").toString();
										
					//get driver gcmid
					
					Filter driverdata = new FilterPredicate("email",
							FilterOperator.EQUAL, driver);
					Query q1 = new Query("driver").setFilter(driverdata);

					PreparedQuery pq1 = datastore.prepare(q1);

					String regId = "";
					@SuppressWarnings("deprecation")
					int count = pq1.countEntities();
					if (count == 0) {
						
					}
					
					else {
						//get drivers gcmid
						Entity res = pq1.asSingleEntity();
						regId = res.getProperty("gcmid").toString();
						
						
						//mark driver as busy
						res.setProperty("state", 1);
						datastore.put(res);
					
						
					}
					
					//send message to driver that taxi is accepted
					
					log.info("got info from datastore from driver table Drvr is "
							+ driver);
					String userMessage =Custid+",Y";
					Sender sender = new Sender(GOOGLE_SERVER_KEY);
					Message message = new Message.Builder().timeToLive(120)
					// .delayWhileIdle(true)
							.addData(MESSAGE_KEY, userMessage).build();
					
					
					
					try {
						sender.send(message, regId, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//delete entry from datastore
					datastore.delete(r.getKey());

					
				}
				
				
				
				else
				{
					try
					{
					  response.setContentType("text/plain");
					  response.getWriter().println("Wrong Customer email entered \n");
					}
					catch(Exception e)
					{
					  Log.warn(e.toString());
					}
					
				}
					
				
		}
		
		else if(choice.toLowerCase().equals("no"))
		{
		
			//user says no. tell driver no
			

			
			Filter cust = new FilterPredicate("cust_email", FilterOperator.EQUAL,Custid);
			   Query q = new Query("matched").setFilter(cust);
			   PreparedQuery pq = datastore.prepare(q);
		
				

				
			
				
				//get driver email
			   Entity r= pq.asSingleEntity();
			   String driver="";
				if (r!=null) {
					driver=r.getProperty("driver_email").toString();
					
					
					//get driver gcmid
					
					Filter driverdata = new FilterPredicate("email",
							FilterOperator.EQUAL, driver);
					Query q1 = new Query("driver").setFilter(driverdata);

					PreparedQuery pq1 = datastore.prepare(q1);

					String regId = "";
					@SuppressWarnings("deprecation")
					int count = pq1.countEntities();
					if (count == 0) {
						
					}
					
					else {
						Entity res = pq1.asSingleEntity();
						regId = res.getProperty("gcmid").toString();

					}
					
					//send message to driver that taxi is accepted
					
					log.info("got info from datastore from driver table Drvr is "
							+ driver);
					String userMessage =Custid+",N";
					Sender sender = new Sender(GOOGLE_SERVER_KEY);
					Message message = new Message.Builder().timeToLive(120)
					// .delayWhileIdle(true)
							.addData(MESSAGE_KEY, userMessage).build();
					
					
					
					try {
						sender.send(message, regId, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//delete entry from datastore
					datastore.delete(r.getKey());

					
				}
				
				
				
				else
				{
					try
					{
					  response.setContentType("text/plain");
					  response.getWriter().println("Wrong Customer email entered \n");
					}
					catch(Exception e)
					{
					  Log.warn(e.toString());
					}
					
				}

		}
		
		
		
		

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		doGet(request, response);
	}
}
