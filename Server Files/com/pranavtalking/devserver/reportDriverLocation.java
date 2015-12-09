package com.pranavtalking.devserver;


import java.io.IOException;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class reportDriverLocation extends HttpServlet {
	
	public reportDriverLocation() {
		super();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	//	Result result = null;

	
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			
			
			
			String name=request.getParameter("name");
			String pass=request.getParameter("pass");
			String lat=request.getParameter("lat");
			String lon=request.getParameter("lon");
			
			if(name.isEmpty() || pass.isEmpty() || lat.isEmpty() ||lon.isEmpty()){
				response.setContentType("text/plain");
		       response.getWriter().println("ERROR1:Field Empty");
			}
			
			Filter driverdata= new  FilterPredicate("email", FilterOperator.EQUAL,name);
			Query q1 = new Query("driver").setFilter(driverdata);

			PreparedQuery pq1 = datastore.prepare(q1);
		   @SuppressWarnings("deprecation")
		int p=pq1.countEntities();
		   if(p==0)
		   {
			   response.setContentType("text/plain");
		       response.getWriter().println("ERROR2:USER DOES NOT EXIST");
		   }
		   else
		   {
			   Entity res=pq1.asSingleEntity();
			   if(res.getProperty("password").toString().equals(pass))
			   {
				   //passwords match. update gcm id send ack
				   
				   res.setProperty("lat",lat);
				   res.setProperty("lon",lon);
					datastore.put(res);
				
					//give auth as encrypted username
					//TODO Encrypted username 
					
					 response.setContentType("text/plain");
		        	response.getWriter().println("SUCCESS");
					
			   }
			   
			   else
			   {
				 response.setContentType("text/plain");
			     response.getWriter().println("ERROR3:Incorrect Password");
			     
			     
			   }
			   
			  
		   }
		   
		
		
			}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			doPost(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
			request.setAttribute("pushStatus", e.toString());
		}
	}

}
