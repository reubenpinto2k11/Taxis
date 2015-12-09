package com.pranavtalking.devserver;


class Data implements Comparable<Data> 
//implements comparable to sort objects
{
	public String lat,lon,id;
	public double distance; //used to store distance from user
	 
	
	 public String getLat()
	 {
		 return lat;
	 }
	 
	 public String getLong()
	 {
		 return lon;
	 }
	 
	 public String getid()
	 {
		 return id;
	 }
	 
	 public double getdist()
	 {
		 return this.distance;
	 }
	 
	 public int compareTo(Data compreData) {
		 //Compares this object with the specified object for order. 
		 //Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.

			double comparedistance = ((Data) compreData).getdist(); 
	 
			//ascending order
			if(this.distance < comparedistance )
				return -1;
			
			else if (this.distance > comparedistance )
			 return 1;
			
			else return 0;
	 
			
	 
		}	
	 
		 
}

