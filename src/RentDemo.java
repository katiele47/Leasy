package comp378_JDBC;

import java.sql.Date;

public class RentDemo {
	public static void main(String[] args) {
		QueryExecutor qe = new QueryExecutor();
		
		Owner o1 = new Owner(110, "Caroline", "Girvan");
		o1.addNewOwner(qe);
		
		Property p1 = new Property(8812, o1.getOwnerID(), "129 Putnam Ave", "New York", 
				"NY", 2, 1, "2022-05-03", 3, 6, 1200, true, true, "Available");
		Property p2 = new Property(8003, o1.getOwnerID(), "234 Pavonia Ave", "New York", 
				"NY", 3, 1, "2022-04-12", 4, 4, 1600, true, false, "Available");
		o1.createProperty(qe, p1);
		o1.createProperty(qe, p2);
		
		o1.createProperty(qe, p1);
		
		Renter r1 = new Renter(235, "Eren", "Jaeger", "New York", 
				"NY", 1, 1, "2022-06-13", 3, 4);
		r1.addNewRenter(qe);
		r1.setFurnishment(true);
		r1.searchAllPropsBy(qe, false, false, true);
		
//		r1.setBudget(1800);
	}
}
