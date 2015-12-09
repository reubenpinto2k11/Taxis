package com.pranavtalking.devserver;


import java.io.IOException;



import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;


@SuppressWarnings("serial")
public class GetTaxi extends HttpServlet{
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException 
    {
		
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		String snd=null;
		int f=0;
		
		Filter stat= new FilterPredicate("state",FilterOperator.NOT_EQUAL,1);
		Query q = new Query("driver").setFilter(stat);
		PreparedQuery pq=ds.prepare(q);	
		
		@SuppressWarnings("deprecation")
		int n=pq.countEntities();
		int i=0;
		String a[]=new String[n];
		
		response.setContentType("text/plain");
		
		for(Entity res : pq.asIterable())
		{
			f=0;
			snd=(String)res.getProperty("taxi_type");
			
			
			if(i==0)
				a[i]=(String)res.getProperty("taxi_type");
			else
			{
			for(int j=0;j<i;j++)
			{
				if(snd.equals(a[j]))
				{
					f=1;
					break;
				}				
			}
			if(f==0)
				a[i]=snd;
			else 
				continue;
			}
			
			response.getWriter().println(a[i]);
			i++;
		}
   
    }
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException
	{
		doPost(request,response);
	}

}
