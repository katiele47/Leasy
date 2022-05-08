package comp378_JDBC;

import java.sql.Date;
import java.util.HashMap;

public class RentDemo {
	
	// Create a hashmap for keeping track of all created objects
	static HashMap<Integer, Owner> ownerMap = new HashMap<>();
	static HashMap<Integer, Renter> renterMap = new HashMap<>();
	static HashMap<Integer, Property> propMap = new HashMap<>();
	
	public static void main(String[] args) {
		QueryExecutor qe = new QueryExecutor();
		
		// Initialize owner object
		Owner o1 = new Owner(110, "Caroline", "Girvan");
		Owner o2 = new Owner(342, "Mikasa", "Ackerman");
		
		// Add new renter to database
		o1.addNewOwner(qe);
		o2.addNewOwner(qe);
		
		ownerMap.put(o1.getOwnerID(), o1);
		ownerMap.put(o2.getOwnerID(), o2);
		
		// Initialize property objects
		Property p1 = new Property(8100, o1.getOwnerID(), "129 Putnam Ave", "New York", 
				"NY", 2, 1, "2022-05-03", 3, 6, 1200, "Yes", "Yes", "Available");
		Property p2 = new Property(8200, o1.getOwnerID(), "234 Pavonia Ave", "New York", 
				"NY", 3, 1, "2022-04-12", 4, 4, 1600, "Yes", "No", "Available");
		
		o1.createProperty(qe, p1);
		o1.createProperty(qe, p2);
		
		Property p3 = new Property(8300, o2.getOwnerID(), "376 West 55th Street", "New York", 
				"NY", 2, 2, "2022-06-10", 3, 5, 1750, "No", "Yes", "Available");
		
		// Add properties to database
		
		o2.createProperty(qe, p3);
		
		propMap.put(p1.getPropID(), p1);
		propMap.put(p2.getPropID(), p2);
		propMap.put(p3.getPropID(), p3);
		
		// Initialize renter object
		Renter r1 = new Renter(201, "Eren", "Jaeger", "New York", 
				"NY", 1, 1, "2022-06-13", 3, 4);
		Renter r2 = new Renter(202, "Erik", "Herman", "New York", 
				"NY", 1, 1, "2022-05-14", 1, 2);
		Renter r3 = new Renter(203, "Jess", "Wang", "New York", 
				"NY", 1, 1, "2022-05-22", 5, 6);
		Renter r4 = new Renter(204, "Levi", "Ackerman", "New York", 
				"NY", 1, 1, "2022-05-18", 4, 12);
		
		// Add new renter to database
		r1.addNewRenter(qe);
		r2.addNewRenter(qe);
		r3.addNewRenter(qe);
		r4.addNewRenter(qe);
		
		renterMap.put(r1.getRenterID(), r1);
		renterMap.put(r2.getRenterID(), r2);
		renterMap.put(r3.getRenterID(), r3);
		renterMap.put(r4.getRenterID(), r4);
		
		// Set renter's optional fields
		r1.setLaundry(qe, "Yes"); // Indoors laundry included
		r1.setFurnishing(qe, "No"); // Unfurnished
		r1.setBudget(qe, 1500);
		
		// Update the new budget for renter r1
		r1.setBudget(qe, 1800);
		
		r2.setBudget(qe, 1100);
		
		r3.setLaundry(qe, "Yes");
		r3.setFurnishing(qe, "Yes");
		r3.setBudget(qe, 2200);
		
		r4.setLaundry(qe, "Yes");
		
		// Search for properties matching optional conditions
		r1.searchAllPropsBy(qe, false, false, false);
		
		// Renter R1 sends inquiry to the owner of property p3 (342)
		r1.sendInquiry(qe, p3);
		
		// Renter R2
		r2.sendInquiry(qe, p1);
		r2.sendInquiry(qe, p2);
		
		// Renter R3
		r3.sendInquiry(qe, p2);
		r3.sendInquiry(qe, p3);
		
		// [Table = lease (count), owner (received_inquiries)]
		
		// Renter R4
		r4.sendInquiry(qe, p1);
		r4.sendInquiry(qe, p2);
		r4.sendInquiry(qe, p3);
		
		// [DEMO Pause]: Manually set r1 (201) to be "Rented" and property p3 (8300) to be "Taken"
		
		// Owner accepts all valid inquiries 
		o1.acceptInquiry(qe);
		
		// [DEMO Table = inquiry (accepted rows), owner (accepted_inquiries), renter (accepted_inquiries)]
		
		// Owner sends a lease contract to one of the renters who had an accepted inquiry
		o1.sendLeaseContract(qe, r4, p1);
		
		// [DEMO Table = lease (status), inquiry (accepted rows)]
		
		// Renter signs the lease contract for an interested property
		r4.signContract(qe, p1);
		
		// [DEMO Table = lease (status), owner (num_rented_props), renter (status), property (status)]
		
		// Renter pays the monthly rent to the owner of the rented property
		r4.payRent(qe, p1);
		
		// [DEMO Table = lease (status), owner (received_rent), property (monthly_rent)]
	}
}
