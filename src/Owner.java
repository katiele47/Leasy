package Leasy;

import java.util.List;
import java.util.Map;


public class Owner {
	
	public List<Property> propList;
	public List<Property> rentedPropList;
	
	//key-value pair of ownerID mapping to a list of propID
	public Map<Owner, List<Property>> requests;
	
	//key-value pair of ownerID mapping to a single propID
	public Map<Owner, Property> acceptedRequests;
	
	public double rentReceived;
	public double depositReceived;
	
	public Owner(int ownerID, String first, String last, List<Property> propsList,
			List<Property> rentedPropList, Map<Owner, List<Property>> requests, 
			Map<Owner, Property> acceptedRequests, double rentReceived, double depositReceived) {
		
		this.propList = propsList;
		this.rentedPropList = rentedPropList;
		this.requests = requests;
		this.acceptedRequests = acceptedRequests;
		this.rentReceived = rentReceived;
		this.depositReceived = depositReceived;
	}
	
	private int getNumProperties() {
		return this.propList.size();
	}
	
	private static void createProperty(QueryExecutor qe) {
		
	}
	
	private static void acceptLeaseRequest(QueryExecutor qe) {
		// only accept request if Renter's status is not "Rented"
	}
	
	private static void sendLeaseContract(QueryExecutor qe) {
		
	}
	
	private static void acceptDeposit(QueryExecutor qe) {
		
	}
	
	private static void acceptRentPayment(QueryExecutor qe) {
		
	}
	
//	public static void main(String[] args) {
//		QueryExecutor qe = new QueryExecutor();
//		
//		String createProp = "INSERT INTO "
//	
//	}
}
