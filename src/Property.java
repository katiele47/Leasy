package Leasy;

import java.sql.Date;

public class Property {
	
	public int propID;
	public int ownerID;
	public String address;
	public String city;
	public String state;
	
	public double rent;
	public boolean laundry;
	public boolean furnished;

	public int numBedroom;
	public int numBathroom;
	
	public Date dateAvailable;
	public int minStay;
	public int maxStay;
	public String status;
	
	public Property(int propID, int ownerID, String address, String city, 
			String state, double monthlyRent, boolean laundry, boolean furnished,
			int numBedroom, int numBathroom, Date dateAvailable, int minStay,
			int maxStay, String status) {
		this.propID = propID;
		this.ownerID = ownerID;
		this.address = address;
		this.city = city;
		this.state = state;
		this.rent = monthlyRent;
		this.laundry = laundry;
		this.furnished = furnished;
		this.numBedroom = numBedroom;
		this.numBathroom = numBathroom;
		this.dateAvailable = dateAvailable;
		this.minStay = minStay;
		this.maxStay = maxStay;
		
	}
	
	
}
