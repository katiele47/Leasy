package comp378_JDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Renter {
	
	//STATIC FIELDS
	
	//required 
	public int renterID;
	public String first;
	public String last;
	public String city;
	public String state;
	int numBedroom;
	public int numBathroom;
	
	//duration 
	public String date;
	public int minStay;
	public int maxStay;
	
	//optional 
	public double budget = 0;
	public String laundry = "Flexible";
	public String furnished = "Flexible";
	
	
	//DYNAMIC FIELDS
	
	public String actionStatus;
	public List<Property> inquiryList;
	public Property rentedProp;
	
	
	public Renter(int renterID, String first, String last,
			String city, String state, int numBedroom, 
			int numBathroom, String date, int minStay, int maxStay) {
		this.renterID = renterID;
		this.first = first;
		this.last = last;
		this.city = city;
		this.state = state;
		this.numBedroom = numBedroom;
		this.numBathroom =  numBathroom;
		this.date = date;
		this.minStay = minStay;
		this.maxStay = maxStay;
	}
	
	/**
	 * 
	 * Get this renter's ID
	 * 
	 * @return this renter's ID
	 */
	public int getRenterID() {
		return this.renterID;
	}
	
	//Methods for setting optional fields
	
	
	/**
	 * 
	 * Set the renter's limit of monthly rent. 
	 * Note that the default limit is 0, meaning the renter has
	 * a flexible budget.
	 * 
	 * @param qe the Query Executor object
	 * @param budgetLimit the maximum monthly rent the renter can pay for a property
	 */
	public void setBudget(QueryExecutor qe, double budgetLimit) {
		Connection conn = qe.getConnection();
		
		String update = "UPDATE `renter` SET budget = ? WHERE renter_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(update)) {
			
			statement.setDouble(1, budgetLimit);
			statement.setInt(2, this.renterID);
			
			conn.setAutoCommit(false);
			statement.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);
			
			//Only update class instance if transaction succeeds
			this.budget = budgetLimit;

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
	 * Set the renter's in-unit laundry preference to be either "Yes" or "No".
	 * Note that the default option is "Flexible".
	 * 
	 * @param qe the Query Executor object
	 * @param hasLaundry an indicator of whether the property has an in-unit laundry
	 */
	public void setLaundry(QueryExecutor qe, String hasLaundry) {
		Connection conn = qe.getConnection();
		
		String update = "UPDATE `renter` SET laundry = ? WHERE renter_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(update)) {
			
			statement.setString(1, hasLaundry);
			statement.setInt(2, this.renterID);
			
			conn.setAutoCommit(false);
			statement.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);
			
			//Only update this Renter object's instance if the transaction succeeds
			this.laundry = hasLaundry;

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
	 * Set the renter's furnishing preference to be either "Yes" or "No".
	 * Note that the default option is "Flexible".
	 * 
	 * @param qe the Query Executor object
	 * @param isFurnished an indicator of whether the property is furnished or unfurnished
	 */
	public void setFurnishing(QueryExecutor qe, String isFurnished) {
		Connection conn = qe.getConnection();
		
		String update = "UPDATE `renter` SET furnished = ? WHERE renter_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(update)) {
			
			statement.setString(1, isFurnished);
			statement.setInt(2, this.renterID);
			
			conn.setAutoCommit(false);
			statement.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);
			
			//Only update this Renter object's instance if the transaction succeeds
			this.furnished = isFurnished;

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
	 * Add a new Renter object to the table 'renter' in the 'lease' database. 
	 * Fields, or columns, that are optional are not added to the query, except for the following: 
	 * 
	 * - Renter's ID
	 * - First name
	 * - Last name
	 * - City to which the renter is moving
	 * - State of the city to which the renter is moving
	 * - Number of bedrooms 
	 * - Number of bathrooms
	 * - The date on which the renter is looking to move in
	 * - A minimum number of months the renter is looking to lease
	 * - A maximum number of months the renter is looking to lease
	 * 
	 * @param qe the Query Executor object
	 */
	public void addNewRenter(QueryExecutor qe) {
		Connection conn = qe.getConnection();
		
		String addRenterQuery = "INSERT INTO `renter` (renter_id, first_name, last_name,"
				+ "city, state, num_bedrooms, num_bathrooms, date_available, min_stay, max_stay) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d'), ?, ?);";
		
		try (PreparedStatement statement = conn.prepareStatement(addRenterQuery)) {
			
			statement.setInt(1, this.renterID);
			statement.setString(2, this.first);
			statement.setString(3, this.last);
			statement.setString(4, this.city);
			statement.setString(5, this.state);
			statement.setInt(6, this.numBedroom);
			statement.setInt(7, this.numBathroom);
			statement.setString(8, this.date);
			statement.setInt(9, this.minStay);
			statement.setInt(10, this.maxStay);
			
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
	 * Search all properties by optional filters - budget, laundry, and furnished. 
	 * If any of these filters are not specified, they won't be added to the query.
	 * 
	 * Note that the query will set all other filters (city, state, date, etc) to match
	 * the fields initially associated with a renter by default. These fields are required
	 * without the renter explicitly specifying them.
	 * 
	 * Returned property(s) must:
	 * 
	 * - Be in the same city as the renter's city
	 * - Be in the same state as the renter's state
	 * - Have at least the number of bedrooms specified by the renter
	 * - Have at least the number of bathrooms specified by the renter
	 * - Have an available date exactly or before the renter's move-in date
	 * - Not require a minimum stay greater than the renter's maximum stay OR...
	 * - Not require a maximum stay smaller than the renter's minimum stay 
	 * - Be ordered by most recent available dates
	 * 
	 * Unless the renter explicitly specifies laundry, furnishing, or budget, all
	 * optional fields are considered as "flexible" and are not added to the query
	 * 
	 * 
	 * @param qe the Query Executor object
	 * @param byBudget an optional filter matching the the renter's non-zero budget
	 * @param byLaundry an optional filter matching the renter's preference for in-unit laundry
	 * @param byFurnished an optional filter matching the renter's preference for furnishing
	 * 
	 */
	public void searchAllPropsBy(QueryExecutor qe, boolean byBudget, boolean byLaundry,
			boolean byFurnished) {
		
		Connection conn = qe.getConnection();
		
		String searchQuery = ""
				+ "SELECT\n"
				+ "    *\n"
				+ "FROM\n"
				+ "    `property`\n"
				+ "WHERE\n"
				+ "    city = ? \n"
				+ "    AND state = ? \n"
				+ "    AND num_bedrooms >= ? \n"
				+ "    AND num_bathrooms >= ? \n"
				+ "    AND DATE_FORMAT(date_available, '%Y-%m-%d') <= ? \n"
				+ "    AND NOT ((min_stay > ?) OR(max_stay < ?)) \n"
				+ "    AND rent_status = \"Available\"\n"
				+ "ORDER BY\n"
				+ "    date_available "
				+ ";";
				
		
		//Optional fields
		if (byBudget && this.budget > 0) {
			searchQuery = searchQuery + " AND monthly_rent <= " + this.budget;
		}
		if (byLaundry && !this.laundry.equals("Flexible")) {
			searchQuery = searchQuery + " AND laundry = " + "'" + this.laundry + "'";
		}
		if (byFurnished && !this.furnished.equals("Flexible")) {
			searchQuery = searchQuery + " AND furnished = " + "'" + this.furnished + "'";
		}
		searchQuery = searchQuery + ";";
		
		try (PreparedStatement statement = conn.prepareStatement(searchQuery)) {
			//Required fields
			statement.setString(1, this.city);
			statement.setString(2, this.state);
			statement.setInt(3, this.numBedroom);
			statement.setInt(4, this.numBathroom);
			statement.setString(5, this.date);
			statement.setInt(6, this.maxStay);
			statement.setInt(7, this.minStay);
			
			conn.setAutoCommit(false);
			ResultSet resultSet = statement.executeQuery();
			
			conn.commit();
			conn.setAutoCommit(true);
			
//			System.out.println("Query:" + statement);
			while (resultSet.next()) { 
				for (int i=0; i<14; i++) {
					System.out.print(resultSet.getString(i+1) + " | ");
				}
				System.out.println();
			}
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
	
	/**
	 * 
	 * Send an inquiry to the owner of the interested prop. The query performs the following actions:
	 * - Increment the number of the renter's sent inquiries by 1
	 * - Increment the number of the owner's received inquiries by 1
	 * - Add a new record of inquiry using the renter's ID and the property's ID to table "inquiry" 
	 * with the inquiry date equal to the current date
	 * 
	 * @param qe the Query Executor object
	 * @param prop the property the renter inquires
	 */
	public void sendInquiry(QueryExecutor qe, Property prop) {
		Connection conn = qe.getConnection();
		
		int propOwnerID = prop.getOwnerID();
		
		String renterQuery = "UPDATE `renter` SET sent_inquiries = sent_inquiries + 1 "
				+ "WHERE renter_id = ?"; 
		String ownerQuery = "UPDATE `owner` SET received_inquiries = received_inquiries + 1 "
				+ "WHERE owner_id = ?"; 
		String inquiryQuery = "INSERT INTO `inquiry` (renter_id, prop_id, inquire_date, status, owner_id) "
				+ "VALUES (?, ?, CURDATE(), ?, ?)";
		
		try (PreparedStatement statementRenter = conn.prepareStatement(renterQuery);
				PreparedStatement statementOwner = conn.prepareStatement(ownerQuery);
				PreparedStatement statementInquiry = conn.prepareStatement(inquiryQuery)) {
			
			conn.setAutoCommit(false);
			
			// Update table renter
			statementRenter.setInt(1, this.renterID);
			statementRenter.executeUpdate();
			
			// Update table owner
			statementOwner.setInt(1, propOwnerID);
			statementOwner.executeUpdate();
			
			// Update table inquiry
			statementInquiry.setInt(1,  this.renterID);
			statementInquiry.setInt(2,  prop.getPropID());
			statementInquiry.setString(3,  "Received");
			statementInquiry.setInt(4,  propOwnerID);
			statementInquiry.execute();
			
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
	/**
	 * 
	 * Sign the contract for renting a property, assuming that the property is still available.
	 * The renter can only sign the contract if:
	 * 
	 * - The renter has not already secured a lease elsewhere (action status != "Rented")
	 * - There exists a lease record between the renter and the owner of the interested property
	 * 
	 * The query performs the following actions:
	 * 
	 * - Update the renter's action status to be "Rented"
	 * - Update the property's rent status to be "Taken"
	 * - Update the lease record to be "Signed"
	 * - Increment the owner's number of rented properties by 1
	 * 
	 * 
	 * @param qe
	 * @param prop the property for which the renter wants to sign a sublease contract
	 */
	public void signContract(QueryExecutor qe, Property prop) {
		Connection conn = qe.getConnection();
		
		String updateQuery = ""
				+ "UPDATE\n"
				+ "    `renter` r,\n"
				+ "    `property` p,\n"
				+ "    `owner` o,\n"
				+ "    `lease` l1\n"
				+ "SET\n"
				+ "    r.action_status = \"Rented\",\n"
				+ "    p.rent_status = \"Taken\",\n"
				+ "    o.num_rented_props = o.num_rented_props + 1,\n"
				+ "    l1.status = \"Signed\"\n"
				+ "WHERE\n"
				+ "    r.action_status != \"Rented\" AND EXISTS(\n"
				+ "    SELECT\n"
				+ "        *\n"
				+ "    FROM\n"
				+ "        `lease` l2\n"
				+ "    WHERE\n"
				+ "        l2.renter_id = l1.renter_id AND l2.prop_id = p.prop_id "
				+ "        AND l2.status = \"Awaiting signature\"\n"
				+ ") AND p.owner_id = o.owner_id AND r.renter_id = ? AND p.prop_id = ?";
		try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
			
			conn.setAutoCommit(false);
		
			statement.setInt(1, this.getRenterID());
			statement.setInt(2, prop.getPropID());
			statement.executeUpdate();
			
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
	/**
	 * 
	 * Make a monthly payment to the owner of the rented property.
	 * The renter can only pay if:
	 * 
	 * - There exists a signed lease contract record between the renter and 
	 * the owner of the interested property
	 * 
	 * The query performs the following actions:
	 * 
	 * - Update the lease record to be "Paid"
	 * - Add the property's monthly rent to the owner's received payment  
	 * 
	 * @param qe the Query Executor object
	 * @param prop the property for which the renter pays the monthly rent
	 */
	public void payRent(QueryExecutor qe, Property prop) {
		Connection conn = qe.getConnection();
		
		String updateQuery = ""
				+ "UPDATE\n"
				+ "    `renter` r,\n"
				+ "    `property` p,\n"
				+ "    `owner` o,\n"
				+ "    `lease` l1\n"
				+ "SET\n"
				+ "	l1.status = \"Paid\",\n"
				+ "    o.received_rent = o.received_rent + p.monthly_rent\n"
				+ "WHERE\n"
				+ "    EXISTS(\n"
				+ "    SELECT\n"
				+ "        *\n"
				+ "    FROM\n"
				+ "        `lease` l2\n"
				+ "    WHERE\n"
				+ "        l2.renter_id = l1.renter_id AND l2.prop_id = p.prop_id AND l2.status = \"Signed\"\n"
				+ "    ) \n"
				+ "AND p.owner_id = o.owner_id AND r.renter_id = ? AND p.prop_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
			conn.setAutoCommit(false);
			
			statement.setInt(1, this.getRenterID());
			statement.setInt(2, prop.getPropID());
			statement.executeUpdate();
			
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
	
	public static void bookmarkFavoriteProps(QueryExecutor qe) {
		
	}
}
