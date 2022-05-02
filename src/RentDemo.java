package comp378_JDBC;

import java.sql.Date;

public class RentDemo {
	public static void main(String[] args) {
		QueryExecutor qe = new QueryExecutor();
		
		Owner o1 = new Owner(110, "Caroline", "Girvan");
		o1.addNewOwner(qe);
		
		Property p1 = new Property(8812, o1.getOwnerID(), "129 Putnam Ave", "New York", 
				"NY", 1200.00, true, true, 2, 1.5, new Date(System.currentTimeMillis()), 
				3, 6, "Available");
		o1.createProperty(qe, p1);
	}
}
