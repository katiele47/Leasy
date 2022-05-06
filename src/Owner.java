package comp378_JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;


public class Owner {
	
	public int ownerID;
	public String first;
	public String last;
	
	public List<Property> propList;
	public List<Property> rentedPropList;
	
	//key-value pair of ownerID mapping to a list of propID
	public Map<Owner, List<Property>> inquiries;
	
	//key-value pair of ownerID mapping to a single propID
	public Map<Owner, Property> acceptedInquiries;
	
	public double receivedRent;
	
	public Owner(int ownerID, String first, String last) {
		this.ownerID = ownerID;
		this.first = first;
		this.last = last;
	}
	
	public int getOwnerID() {
		return this.ownerID;
	}
	
	public String getFirstName() {
		return this.first;
	}
	
	public String getLastName() {
		return this.last;
	}
	
	public int getNumProperties() {
		return this.propList.size();
	}
	
	public List<Property> getPropsList() {
		return this.propList;
	}
	
	public List<Property> getRentedPropsList() {
		return this.rentedPropList;
	}
	
	public Map<Owner, List<Property>> getInquiries() {
		return this.inquiries;
	}
	
	public Map<Owner, Property> getAcceptedInquiries() {
		return this.acceptedInquiries;
	}
	
	public double getRentReceived() {
		return this.receivedRent;
	}
	
	public void addNewOwner(QueryExecutor qe) {
		Connection conn = qe.getConnection();
		
		String query = "INSERT INTO `owner` (owner_id, first_name, last_name) VALUES (?, ?, ?);";
		
		try (PreparedStatement statement = conn.prepareStatement(query)) {
			
			statement.setInt(1, this.ownerID);
			
			statement.setString(2, this.first);
			
			statement.setString(3, this.last);
			
			conn.setAutoCommit(false);
			statement.execute();
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				System.out.println("Transaction is being rolled back");
				conn.rollback();
			} catch (SQLException e2) {
				qe.handleSQLException(e2);
			}
		}
	}
	/**
	 * 
	 * Add a new property to the database. The query performs the following actions
	 * - Create a new record of the new property in table 'property'
	 * - Increment the property's owner's number of properties by 1
	 * 
	 * @param qe the Query Executor object
	 * @param prop
	 */
	public void createProperty(QueryExecutor qe, Property prop) {
		Connection conn = qe.getConnection();
		
		String propQuery = "INSERT INTO `property` (prop_id, owner_id, address, city, "
				+ "state, monthly_rent, laundry, furnished, num_bedrooms,"
				+ "num_bathrooms, date_available, min_stay, max_stay, rent_status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d'), ?, ?, ?);";
		
		String ownerQuery = "UPDATE `owner` SET num_props = num_props + 1"
				+ " WHERE owner_id = ?;";
		
		try (PreparedStatement statementProp = conn.prepareStatement(propQuery);
				PreparedStatement statementOwner = conn.prepareStatement(ownerQuery)) {
			
			statementProp.setInt(1, prop.getPropID());
			statementProp.setInt(2, this.ownerID);
			statementProp.setString(3, prop.getPropAddress());
			statementProp.setString(4, prop.getPropCity());
			statementProp.setString(5, prop.getPropState());
			statementProp.setDouble(6, prop.getPropRent());
			statementProp.setString(7, prop.getLaundry());
			statementProp.setString(8, prop.getFurnished());
			statementProp.setInt(9, prop.getNumBedroom());
			statementProp.setDouble(10, prop.getNumBathroom());
			statementProp.setString(11, prop.getDateAvailable());
			statementProp.setInt(12, prop.getMinStay());
			statementProp.setInt(13, prop.getMaxStay());
			statementProp.setString(14, prop.getPropStatus());
			
			conn.setAutoCommit(false);
			statementProp.execute();
			
			statementOwner.setInt(1, this.ownerID);
			statementOwner.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				System.out.println("Transaction is being rolled back");
				conn.rollback();
			} catch (SQLException e2) {
				qe.handleSQLException(e2);
			}
		}
	}
	
	/**
	 * 
	 * Accept all inquiries sent by renter(s) regarding some properties owned by this owner.
	 * If the renter's action status is "Rented", meaning the renter had already secured
	 * a lease elsewhere, the owner will automatically ignore the inquiry.
	 * 
	 * - Count the number of inquiries in the table 'inquiry' whose statuses are "Received" (Not yet accepted)
	 * and sent by renters who had not already secured a lease elsewhere (renter's action status != "Rented").
	 * If the properties are no longer available, inquiries will be ignored. Finally, add this number to this 
	 * owner's current total number of accepted inquiries.
	 * 
	 * - Update the status of the inquiries meeting the above requirement to be "Accepted"
	 * 
	 * - Update all renters' number of accepted inquiries according to the changes above.
	 * 
	 * @param qe the Query Executor object
	 */
	public void acceptInquiry(QueryExecutor qe) {
		Connection conn = qe.getConnection();
		
		String inquiryQuery = "UPDATE `inquiry` i, `renter` r, `property` p, `owner` o "
				+ "SET i.status = \"Accepted\" "
				+ "WHERE r.action_status <> \"Rented\" "
				+ "AND i.renter_id = r.renter_id "
				+ "AND p.rent_status = \"Available\" "
				+ "AND i.prop_id = p.prop_id "
				+ "AND i.owner_id = ?;";
		
		String ownerQuery = "UPDATE `owner` o SET "
				+ "o.accepted_inquiries = "
				+ "( "
				+ "SELECT COUNT(*) FROM `inquiry` i "
				+ "WHERE i.status = \"Accepted\" "
				+ "AND i.owner_id = ?"
				+ ")"
				+ "WHERE o.owner_id = ?";
		
		// Update the renters whose inquiries got accepted by traversing the table "inquiry" again, checking for
		// accepted records. Note that some of the accepted records might have been accepted a long time ago.
		String renterQuery = "UPDATE `renter` r SET accepted_inquiries = (SELECT COUNT(*) FROM `inquiry` i "
				+ "WHERE i.status = \"Accepted\" AND i.renter_id = r.renter_id)";
		
//		UPDATE `owner` o SET o.accepted_inquiries = o.accepted_inquiries + (SELECT COUNT(*) FROM `inquiry` i, `renter` r, `property` p WHERE i.status = "Received" AND r.action_status <> "Rented" AND i.renter_id = r.renter_id AND p.rent_status = "Available" AND i.prop_id = p.prop_id) AND o.owner_id = 110;
		
		try (PreparedStatement statementInquiry = conn.prepareStatement(inquiryQuery);
				PreparedStatement statementOwner = conn.prepareStatement(ownerQuery);
				PreparedStatement statementRenter = conn.prepareStatement(renterQuery)) {
			
			conn.setAutoCommit(false);
			
			statementInquiry.setInt(1, this.ownerID);
			statementInquiry.executeUpdate();
			
			statementOwner.setInt(1, this.ownerID);
			statementOwner.setInt(2, this.ownerID);
			statementOwner.executeUpdate();
			
			statementRenter.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				System.out.println("Transaction is being rolled back");
				conn.rollback();
			} catch (SQLException e2) {
				qe.handleSQLException(e2);
			}
		}
	}
	
	public void sendLeaseContract(QueryExecutor qe) {
		
	}
	
	public void acceptRentPayment(QueryExecutor qe) {
		
	}
}
