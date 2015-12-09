package com.pranavtalking.devserver;


import java.io.IOException;
import java.io.PrintWriter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class register extends HttpServlet {
		
	
	
	public register() {
		super();
	}

	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest rq, HttpServletResponse rp)
			throws IOException, ServletException {
		 String phone,email=null,name,pass,t_type,t_num;
		 //String uname;
		 
		 if(rq.getParameter("type").equals("user"))
		 {
			if (rq.getParameter("email") != null) 
			{
			   
			   
				email = rq.getParameter("email");
				name = rq.getParameter("name");
				pass = rq.getParameter("pass");
				phone = rq.getParameter("phone");

				DatastoreService ds = DatastoreServiceFactory
						.getDatastoreService();
				
				Filter driverdata= new  FilterPredicate("email", FilterOperator.EQUAL,email);
				Query q1 = new Query("user").setFilter(driverdata);

				PreparedQuery pq1 = ds.prepare(q1);
			   if( pq1.countEntities() ==0)
			   {
			   

				
				Entity a = new Entity("user", email);
				a.setProperty("email", email);
				a.setProperty("name", name);
				a.setProperty("password", pass);
				a.setProperty("phone", phone);
				a.setProperty("gcmid", "not existing");

				ds.put(a);

			   }
			   
			   
			   else
			   {
					String msg = "User already Exixts";
					PrintWriter out = rp.getWriter();
					out.println(msg);
			   }
				/*
				 * String msg="SUCCESS"; PrintWriter out = rp.getWriter();
				 * out.println(msg);
				 */

			} else {
				String msg = "ERROR";
				PrintWriter out = rp.getWriter();
				out.println(msg);
			}
		 }
		 else
		 {
			 if(rq.getParameter("type").equals("driver"))
			 {
				 //uname=rq.getParameter("username");
				 name = rq.getParameter("name");
				 email = rq.getParameter("email");
				 pass = rq.getParameter("pass");
				 phone = rq.getParameter("mob");
				 t_type=rq.getParameter("t_type");
				 t_num=rq.getParameter("t_num");
				 
				 DatastoreService ds = DatastoreServiceFactory
							.getDatastoreService();
				 
					Filter driverdata= new  FilterPredicate("email", FilterOperator.EQUAL,email);
					Query q1 = new Query("driver").setFilter(driverdata);

					PreparedQuery pq1 = ds.prepare(q1);
				   if( pq1.countEntities() ==0)
				   {
				   

					   
					Entity b = new Entity("driver", email);
					b.setProperty("email",email);
					//b.setProperty("Username",uname);
					b.setProperty("name", name);
					b.setProperty("password", pass);
					b.setProperty("phone", phone);
					b.setProperty("taxi_type", t_type);
					b.setProperty("taxi_number", t_num);
					b.setProperty("gcmid", "New User");
					
				
					b.setProperty("lat",0);
					b.setProperty("lon",0);
					b.setProperty("state",1);
					
					ds.put(b);
				   }
				   
				   

				 
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
