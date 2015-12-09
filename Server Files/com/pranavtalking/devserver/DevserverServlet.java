package com.pranavtalking.devserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;
import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;

import java.util.logging.Logger;



public class DevserverServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	static final String MESSAGE_KEY = "TestMessage";
    private static final Logger log = Logger.getLogger(DevserverServlet.class.getName());

	
	
	
	@SuppressWarnings("deprecation")
	public void doGet(HttpServletRequest rq,HttpServletResponse rp) throws IOException
	{
		//get lat and long from user and gives best taxi
		/*
		 * workflow:
		 * 	query datastore
		 *  use haversine
		 *  call google
		 *  get taxi
		 *  state  all taxis state as busy till reply is received
		 */
		log.log(Level.INFO, "a info msg"); //info
		log.log(Level.WARNING, "a warning msg"); //warning
	//	log.log(Level.FINEST, "a fine(st) msg"); //debug (as finest)
				
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    try
     {
    	
    		   log.log(Level.WARNING, "first"); //warning

    	

	   float usrlat= Float.parseFloat(rq.getParameter("lat"));
	   float usrlon=Float.parseFloat(rq.getParameter("lon"));
	   String destination=rq.getParameter("destination");
	   String type=rq.getParameter("type");
	   String Cust_email=rq.getParameter("id");
	
	   log.log(Level.WARNING, "Got request strings"); //warning
		//	
	   
	   //check within latitude boundaries & taxi type
	   Filter taxitype = new FilterPredicate("taxi_type",FilterOperator.EQUAL,type);
	   
	   Filter latitudegt =
			   new FilterPredicate("lat",
			                       FilterOperator.LESS_THAN_OR_EQUAL,
			                       usrlat+0.25);
	   Filter latitudelt =
			   new FilterPredicate("lat",
			                       FilterOperator.GREATER_THAN_OR_EQUAL,
			                       usrlat-0.25);
	   Filter latfilter= CompositeFilterOperator.and(latitudegt,latitudelt);
	   
	   Filter Coordntyp=null;
	   
	   if(type.equals("Any"))
	   {
		    Coordntyp=latfilter; 
		    		//CompositeFilterOperator.and(latfilter,null);
	   }
	   
	   else
		   { Coordntyp= CompositeFilterOperator.and(latfilter,taxitype);}
	  
	   Filter busy= new  FilterPredicate("state", FilterOperator.EQUAL,0); 
	   
	   //check if not busy
	 
		   Filter totalfilter=CompositeFilterOperator.and(Coordntyp,busy);
			
	
	   
	   
	   //create and fire query
	   Query q = new Query("driver").setFilter(totalfilter);
	   
	   PreparedQuery pq = datastore.prepare(q);
	   log.log(Level.WARNING, "preparedquery executed"); //warning
		//	
	   
	int p=pq.countEntities();
	log.log(Level.WARNING, "got "+p+"results"); //warning
	//	
	

	   if(pq.countEntities()!=0 )
	   {
	   //store results in a[]
	   Data datastoreResult[] = new Data[pq.countEntities()];
	   for( int x1=0; x1<pq.countEntities(); x1++ )
		     datastoreResult[x1]= new Data();

	   int i=0;
	   for (Entity result : pq.asIterable()) 
	   {
		   //check for latitude here.  workaround implemented, Change ASAP
		   if(Float.parseFloat( result.getProperty("lon").toString()) < usrlon+0.25  && Float.parseFloat( result.getProperty("lon").toString()) > usrlon-0.25  )
			{ 
			   String val= result.getProperty("email").toString();
			   datastoreResult[i].id= val;//.toString();
			   val= result.getProperty("lat").toString();
			   datastoreResult[i].lat= result.getProperty("lat").toString();
			   datastoreResult[i].lon=result.getProperty("lon").toString();
			   i++;
			}
	   }
	    log.log(Level.WARNING,"got info from datastore p="+p+"datstore result[0].id "+datastoreResult[0].id);

	
	   
	   Data haversineresult[] = haversine(datastoreResult,usrlat,usrlon);
	   
	   Data finalThree[]=  Callgoogle(haversineresult,usrlat,usrlon);
	   
	    log.log(Level.WARNING,"Customer "+ Cust_email+"Request received, first driver is "+finalThree[0].id);
	   
	    Date date= new Date();
	   //store finalthree in the table with timestamps
	   Entity loc= new Entity("chosenlist",Cust_email); //kind, key
	   loc.setProperty("cust_email", Cust_email);
	   loc.setProperty("destination", destination);
	   loc.setProperty("chosen","0");
	   loc.setProperty("time",""+date.getTime()); //fore to string
	   
	   rp.setContentType("text/plain");
       rp.getWriter().println("created table chosenlist \n");
	   
	   
	   if(finalThree.length>=1)
	   {loc.setProperty("Driver1",finalThree[0].id);
	   loc.setProperty("Driver1Lat",finalThree[0].lat);
	   loc.setProperty("Driver1Long",finalThree[0].lon);

	   rp.setContentType("text/plain");
       rp.getWriter().println(" \n"+finalThree[0].id);
	   
	   }
	   
	   		   
	   if(finalThree.length>=2){
	   loc.setProperty("Driver2",finalThree[1].id);
	   loc.setProperty("Driver2Lat",finalThree[1].lat);
	   loc.setProperty("Driver2Long",finalThree[1].lon);

	   rp.setContentType("text/plain");
       rp.getWriter().println(" \n"+finalThree[1].id);
       
	   }
	   
	    if(finalThree.length>=3)
	   {
	   loc.setProperty("Driver3",finalThree[2].id);
	   loc.setProperty("Driver3Lat",finalThree[2].lat);
	   loc.setProperty("Driver3Long",finalThree[2].lon);

	   rp.setContentType("text/plain");
       rp.getWriter().println(" \n"+finalThree[2].id);

	   }
	   
	   if(finalThree.length==0)
	   {
		   rp.setContentType("text/plain");
	       rp.getWriter().println("NO TAXI FOUND");
	       
	   }
	   
	   else{
	   
		    log.log(Level.WARNING,"CHosenlist put in datastore");

		   datastore.put(loc);
		   //call the url of checking
	   Cronjob x= new Cronjob();
	   x.doGet(null, null);
	   }
	   
	   }
	      
	   else
	   {
		   //no taxis found		   
		   rp.setContentType("text/plain");
	       rp.getWriter().println("{\"id\":\"\",\"lat\":\"\",\"long\":\"\"}");
	   }
	  
   }
   
   catch(NullPointerException np)
   {
	   rp.setContentType("text/plain");
       rp.getWriter().println("Wrong input: check get parameters and try again");
       rp.getWriter().println(np.toString());
	      }
   catch(Exception e)
   {
	   
	   rp.setContentType("text/plain");
       rp.getWriter().println(e.toString());
	   
   }
	   
	   
	   
	 //   @SuppressWarnings("deprecation")
//		String Decode = URLDecoder.decode(jsonString);

	}
	
	
	
	   public static Data[] haversine(Data[] datastoreResult ,float usrlat,float usrlon) {
		   
		   //gets distance of all using haversine formula and return best 3
		   final double R = 6372.8; // In kilometers
		   
		   for(Data item: datastoreResult)
		   {
			   double dLat = Math.toRadians(Float.parseFloat(item.lat) - usrlat);
			   double dLon = Math.toRadians(Float.parseFloat(item.lon) - usrlon);
		        double lat1 = Math.toRadians((Float.parseFloat(item.lat)));
		        double lat2 = Math.toRadians(usrlat);
		 
		        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		        double c = 2 * Math.asin(Math.sqrt(a));
		        if(R*c>0.1) //scam
		        item.distance=R * c;
		        else
		        	item.distance=0.1;
			 }
		   
		   Arrays.sort(datastoreResult);
		   
		   if(datastoreResult.length>=3)
				   return Arrays.copyOf(datastoreResult, 3); //return 3 smallest distances
		   else
			   return datastoreResult;
		 		    
	   
	    }
	   
	   
	   
	   public static Data[] Callgoogle(Data[] datastoreResult ,float usrlat,float usrlon)
	   {
		   for(Data item : datastoreResult)
		   {
			   double x = mapsApi(Double.parseDouble(item.lat), Double.parseDouble(item.lon), usrlat, usrlon);
			   if(x>0.1)              //scam
			        item.distance=x;
			        else
			        	item.distance=0.1;
		   }
		   
		  
		   
		   Arrays.sort(datastoreResult);
		   
		   return datastoreResult;
	   
	   }
	   
	     
	 public static double mapsApi(double lat1, double lon1, double lat2, double lon2) {
		   //http://maps.googleapis.com/maps/api/directions/json?origin=15.6000,73.8200&destination=15.4989,73.8278&sensor=true 
		   		   
		   /*
		    * use distance.value which will give dist in meters
		    * Status Codes

			The "status" field within the Directions response object contains the status of the request, and may contain debugging information to help you track down why the Directions service failed. The "status" field may contain the following values:

			OK 			indicates the response contains a valid result.
			NOT_FOUND 			indicates at least one of the locations specified in the requests's origin, destination, or waypoints could not be geocoded.
			ZERO_RESULTS			 indicates no route could be found between the origin and destination.
			MAX_WAYPOINTS_EXCEEDED 			indicates that too many waypointss were provided in the request The maximum allowed waypoints is 8, plus the origin, and destination. ( Google Maps API for Business customers may contain requests with up to 23 waypoints.)
			INVALID_REQUEST 			indicates that the provided request was invalid. Common causes of this status include an invalid parameter or parameter value.
			OVER_QUERY_LIMIT			 indicates the service has received too many requests from your application within the allowed time period.
			REQUEST_DENIED 			indicates that the service denied use of the directions service by your application.
			UNKNOWN_ERROR 			indicates a directions request could not be processed due to a server error. The request may succeed if you try again.
		    * */
		   
		   
		   //String url = "http://maps.googleapis.com/maps/api/directions/json?origin=15.6000,73.8200&destination=15.4989,73.8278&sensor=true ";
		  String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+lat1+","+lon1+"&destination="+lat2+","+lon2+"&sensor=true ";
		   try
		   {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			// optional default is GET
			con.setRequestMethod("GET");
	 
			//add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
	 
			int responseCode = con.getResponseCode();

			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			
			String resp= response.toString();
			int loc= resp.indexOf("value");
			loc=loc+9;
			
			resp=resp.substring(loc);
			
			 int length = resp.length();
			   String result = "";
			    for (int i = 0; i < length; i++) {
			        Character character = resp.charAt(i);
			        if (Character.isDigit(character)) {
			            result += character;
			        }
			        else break;
			    }
			    //System.out.println("result is: " + result);
			double distance = Integer.parseInt(result) /1000.0;
			
			//System.out.println(response.toString());
			  return distance;
			
			
		   }
		   
		   catch(Exception e){
			   
			   System.out.print(e.toString());
			   //handle this
			   return 0;
		 		   }
			
		 
	   }
	    
	
	
	
	
public void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException 
    {	
	doGet(request, response);
   
    }

}





