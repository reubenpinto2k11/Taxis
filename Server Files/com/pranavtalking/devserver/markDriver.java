package com.pranavtalking.devserver;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class markDriver extends HttpServlet {
    private static final Logger log = Logger.getLogger(DevserverServlet.class.getName());

	
	public markDriver() {
		super();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	//	Result result = null;

	
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();

			
			
			String name=request.getParameter("email");
			String pass=request.getParameter("pass");
			String state=request.getParameter("state");
			
			if(name.isEmpty() || pass.isEmpty() || state.isEmpty()){
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
				log.log(Level.WARNING, "Driver not found"); //warning

			   response.setContentType("text/plain");
		       response.getWriter().println("ERROR2:USER DOES NOT EXIST");
		   }
		   else
		   {
			   Entity res=pq1.asSingleEntity();
				//log.log(Level.WARNING, "got entitys:); //warning

			   if(res.getProperty("password").toString().equals(pass))
			   {
					log.log(Level.WARNING, "passwords match"); //warning

				   //passwords match. update gcm id send ack
				   if(state.toLowerCase().equals("free"))
				   {
					   res.setProperty("state",0);
						log.log(Level.WARNING, "Marked free"); //warning

				   }
				   else if(state.toLowerCase().equals("busy"))
				   {   res.setProperty("state",1);
					log.log(Level.WARNING, "marked busy"); //warning

				   
				   }
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
