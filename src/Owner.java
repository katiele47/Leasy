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
	
	/**
	 * 
	 * Add a new Owner object to the table 'owner' in the 'lease' database. 
	 * The Owner object must have at lease the following fields/columns: 
	 * 
	 * - Owner's ID
	 * - First name
	 * - Last name
	 * 
	 * @param qe the Query Executor object
	 * 
	 */
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
	 * - Increment the property's owner's number of owned properties by 1
	 * 
	 * @param qe the Query Executor object
	 * @param prop the property owned by an existing owner
	 */
	public void createProperty(QueryExecutor qe, Property prop) {
		Connection conn = qe.getConnection();
		
		String propQuery = ""
				+ "INSERT INTO `property`(\n"
				+ "    prop_id,\n"
				+ "    owner_id,\n"
				+ "    address,\n"
				+ "    city,\n"
				+ "    state,\n"
				+ "    monthly_rent,\n"
				+ "    laundry,\n"
				+ "    furnished,\n"
				+ "    num_bedrooms,\n"
				+ "    num_bathrooms,\n"
				+ "    date_available,\n"
				+ "    min_stay,\n"
				+ "    max_stay,\n"
				+ "    rent_status\n"
				+ ")\n"
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d'), ?, ?, ?\n"
				+ ");";
				
		String ownerQuery = ""
				+ "UPDATE\n"
				+ "    `owner`\n"
				+ "SET\n"
				+ "    num_props = num_props + 1\n"
				+ "WHERE\n"
				+ "    owner_id = ?"
				+ ";";	
		
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
	 * Accept all inquiries sent by renter(s) regarding the property(s) owned by this owner.
	 * 
	 * Requirement for an inquiry to be accepted:
	 * - There exists an inquiry between a renter and a property with the status "Received" in table 'inquiry'
	 * - The renter must have not already secured a lease elsewhere (action status != "Rented")
	 * - The inquired property must still be available 
	 * - The renter's move-in city matches the inquired property's city
	 * - The renter's move-in state matches the inquired property's state
	 * - The renter's move-in date matches the inquired property's available date
	 * - The inquired property's minimum stay must not be greater than the renter's maximum stay AND..
	 * - The inquired property's maximum stay must not be smaller than the renter's minimum stay
	 * 
	 * If any of the above conditions are not met, the inquiry will be ignored
	 * 
	 * The query perform the following actions:
	 * - Update the status of the inquiries meeting the above requirement to be "Accepted"
	 * - Count the number of inquiries whose statuses are "Accepted" by this owner. Then, update this
	 * owner's current number of accepted inquiries to be this new number.
	 * - Count the number of inquiries whose statuses are "Accepted" of all existing renters. The,
	 * update all renters' number of accepted inquiries to be these new numbers.
	 * 
	 * @param qe the Query Executor object
	 */
	public void acceptInquiry(QueryExecutor qe) {
		Connection conn = qe.getConnection();
		
		String inquiryQuery = ""
				+ "UPDATE\n"
				+ "    `inquiry` i,\n"
				+ "    `renter` r,\n"
				+ "    `property` p\n"
				+ "SET\n"
				+ "    i.status = \"Accepted\"\n"
				+ "WHERE\n"
				+ "	   i.renter_id = r.renter_id \n"
				+ "    AND i.prop_id = p.prop_id AND r.action_status <> \"Rented\" AND p.rent_status = \"Available\"\n"
				+ "    AND r.city = p.city\n"
				+ "    AND r.state = p.state\n"
				+ "    AND DATE_FORMAT(p.date_available, '%Y-%m-%d') <= DATE_FORMAT(r.date_available, '%Y-%m-%d')\n"
				+ "    AND NOT ((p.min_stay > r.max_stay) OR (p.max_stay < r.min_stay))\n"
				+ "    AND i.owner_id = ?"
				+ ";";
		
		
		String ownerQuery = ""
				+ "UPDATE\n"
				+ "    `owner` o\n"
				+ "SET\n"
				+ "    o.accepted_inquiries =(\n"
				+ "    SELECT\n"
				+ "        COUNT(*)\n"
				+ "    FROM\n"
				+ "        `inquiry` i\n"
				+ "    WHERE\n"
				+ "        i.status = \"Accepted\" "
				+ "		   AND i.owner_id = ?\n"
				+ ")\n"
				+ "WHERE\n"
				+ "    o.owner_id = ?"
				+ ";";
				
		
		// Update the renters whose inquiries got accepted using a full scan of the table
		String renterQuery = ""
				+ "UPDATE\n"
				+ "    `renter` r\n"
				+ "SET\n"
				+ "    accepted_inquiries =(\n"
				+ "    SELECT\n"
				+ "        COUNT(*)\n"
				+ "    FROM\n"
				+ "        `inquiry` i\n"
				+ "    WHERE\n"
				+ "        i.status = \"Accepted\" \n"
				+ "        AND i.renter_id = r.renter_id\n"
				+ ");";
		
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
	/**
	 * 
	 * Send a contract to a renter who has an existing record of accepted inquiry in the 
	 * table 'inquiry'. The renter must have not already rented a place elsewhere and the 
	 * property must be available. 
	 * 
	 * This is done by creating a lease record in the table 'lease'
	 * and setting the status to "Awaiting signature".
	 * 
	 * 
	 * @param qe the Query Executor object
	 * @param renter the renter to whom the owner sends a contract
	 */
	public void sendLeaseContract(QueryExecutor qe, Renter renter, Property prop) {
		Connection conn = qe.getConnection();
		
		int renterID = renter.getRenterID();
		int propId = prop.getPropID();
		int ownerID = this.getOwnerID();
		
		String leaseQuery = ""
				+ "INSERT INTO `lease`(\n"
				+ "    renter_id,\n"
				+ "    prop_id,\n"
				+ "    lease_date,\n"
				+ "	   status,\n"
				+ "    owner_id\n"
				+ ")\n"
				+ "SELECT\n"
				+ "    i.renter_id,\n"
				+ "    i.prop_id,\n"
				+ "    CURDATE(), \n"
				+ "    \"Awaiting signature\", \n"
				+ "    i.owner_id\n"
				+ "FROM\n"
				+ "    `inquiry` i, `renter` r, `property` p \n"
				+ "WHERE\n"
				+ "    i.renter_id = ? \n"
				+ "    AND i.prop_id = ? \n"
				+ "    AND i.owner_id = ? \n"
				+ "    AND i.renter_id = r.renter_id \n"
				+ "    AND r.action_status != \"Rented\"\n"
				+ "    AND i.prop_id = p.prop_id \n"
				+ "    AND p.rent_status = \"Available\" \n"
				+ "    AND i.status = \"Accepted\";";
		
		try (PreparedStatement statement = conn.prepareStatement(leaseQuery)) {
			
			statement.setInt(1, renterID);
			statement.setInt(2, propId);
			statement.setInt(3, ownerID);
			
			conn.setAutoCommit(false);
			statement.execute();
			
			conn.commit();
			conn.setAutoCommit(true);
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				System.out.println("Transaction is being rolled back");
				conn.rollback();
			} catch (SQLException e2) {
				qe.handleSQLException(e2);
			}
		}
	}
}
