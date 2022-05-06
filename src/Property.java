package comp378_JDBC;

import java.sql.Date;

public class Property {
	
	public int propID;
	public int ownerID;
	public String address;
	public String city;
	public String state;
	
	public double rent;
	public String laundry;
	public String furnished;

	public int numBedroom;
	public double numBathroom;
	
	public String dateAvailable;
	public int minStay;
	public int maxStay;
	public String status;
	
	public Property(int propID, int ownerID, String address, String city, 
			String state, int numBedroom, double numBathroom, String dateAvailable, 
			int minStay, int maxStay, double monthlyRent, String laundry, 
			String furnished, String status) {
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
		this.status = status;
		
	}
	public int getPropID() {
		return this.propID;
	}
	public int getOwnerID() {
		return this.ownerID;
	}
	public String getPropAddress() {
		return this.address;
	}
	public String getPropCity() {
		return this.city;
	}
	public String getPropState() {
		return this.state;
	}
	public double getPropRent() {
		return this.rent;
	}
	public String getLaundry() {
		return this.laundry;
	}
	public String getFurnished() {
		return this.furnished;
	}
	public int getNumBedroom() {
		return this.numBedroom;
	}
	public double getNumBathroom() {
		return this.numBathroom;
	}
	public String getDateAvailable() {
		return this.dateAvailable;
	}
	public int getMinStay() {
		return this.minStay;
	}
	public int getMaxStay() {
		return this.maxStay;
	}
	public String getPropStatus() {
		return this.status;
	}
	
}
