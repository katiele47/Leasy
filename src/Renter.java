package Leasy;

import java.util.List;

public class Renter {
	
	public int renterID;
	public String first;
	public String last;
	public double budget;
	public String city;
	public String actionStatus;
	public List<Property> requestList;
	public Property rentedProp;
	
	public Renter(int renterID, String first, String last, double budget,
			String city, String actionStatus, List<Property> requestList, Property rentedProp) {
		this.renterID = renterID;
		this.first = first;
		this.last = last;
		this.budget = budget;
		this.city = city;
		this.actionStatus = actionStatus;
		this.requestList = requestList;
		this.rentedProp = rentedProp;
	}
	
	private static void sendRequest(QueryExecutor qe) {
	
	}
	
	private static void scheduleTour(QueryExecutor qe) {
		
	}
	
	private static void signContract(QueryExecutor qe) {
		//Updated action status to be "Rented"
	}
	
	private static void payRent(QueryExecutor qe) {
		
	}
}
