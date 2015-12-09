package com.pranavtalking.devserver;

//Handles gcm registration as well as user Authentication
//uses entity driver(name,pass,gcmid,...)


import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import java.io.IOException;




import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class auth extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//private static final String GOOGLE_SERVER_KEY = "AIzaSyDef5-US07A95ckj030mwMboJqVwWCQH74";
	static final String MESSAGE_KEY = "TestMessage";


	//private static final String pvtkey="MIGrAgEAAiEAwf1kbzioIumUFfsJAgh6th81HIK96gcqSuchN6RT9ckCAwEAAQIgBOkgMLyTbQbSq9fF+5oxWP2VNNiTvz5bq3deoAUx5x0CEQDtUcDHu0yrJKRoqQ2W8ycXAhEA0UKAVdiECVNLmXWimJPWHwIQHCacdZTULWD1V6zl/cfuFQIRALLeaDp452vdMirLfZ90BrsCEQCoB+Xqudrf+cGLt7W8oMqx";

	public auth() {
		super();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		//Result result = null;

		if (request.getParameter("type").equals("driver")) {
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();



			String name=request.getParameter("name");
			String pass=request.getParameter("pass");
			String regId=request.getParameter("regId");

			if(name.isEmpty() || pass.isEmpty() || regId.isEmpty()){
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

					res.setProperty("gcmid", regId);
					datastore.put(res);

					//give auth as encrypted username
					//TODO Encrypted username 

					response.setContentType("text/plain");
				//	response.getWriter().println(name);
					response.getWriter().println(res.getProperty("email").toString());

				}

				else
				{
					response.setContentType("text/plain");
					response.getWriter().println("ERROR3:Incorrect Password");


				}


			}

		}// driver if ends

		else if (request.getParameter("type").equals("user")) {
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();



			String name=request.getParameter("name");
			String pass=request.getParameter("pass");
			String regId=request.getParameter("regId");

			if(name.isEmpty() || pass.isEmpty() || regId.isEmpty()){
				response.setContentType("text/plain");
				response.getWriter().println("ERROR1:Field Empty");
			}

			Filter driverdata= new  FilterPredicate("email", FilterOperator.EQUAL,name);
			Query q1 = new Query("user").setFilter(driverdata);

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

					res.setProperty("gcmid", regId);
					datastore.put(res);

					//give auth as encrypted username
					//TODO Encrypted username 

					response.setContentType("text/plain");
					response.getWriter().println(res.getProperty("email").toString());

				}

				else
				{
					response.setContentType("text/plain");
					response.getWriter().println("ERROR3:Incorrect Password");


				}


			}

		}// user


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
