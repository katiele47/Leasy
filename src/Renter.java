package comp378_JDBC;

import java.util.List;

public class Renter {
	
	public int renterID;
	public String first;
	public String last;
	public double budget;
	public String city;
	public int numBedroom;
	public int numBathroom;
	public String actionStatus;
	public List<Property> inquiryList;
	public Property rentedProp;
	
	public Renter(int renterID, String first, String last, double budget,
			String city, int numBedroom, int numBathroom, String actionStatus, List<Property> inquiryList, Property rentedProp) {
		this.renterID = renterID;
		this.first = first;
		this.last = last;
		this.budget = budget;
		this.city = city;
		this.numBedroom = numBedroom;
		this.numBathroom =  numBathroom;
		this.actionStatus = actionStatus;
		this.inquiryList = inquiryList;
		this.rentedProp = rentedProp;
	}
	
	private static void sendInquiry(QueryExecutor qe) {
	
	}
	
	private static void signContract(QueryExecutor qe) {
		//Updated action status to be "Rented", add to  LEASE table with "Unpaid" status
	}
	
	private static void payRent(QueryExecutor qe) {
		
	}
}
