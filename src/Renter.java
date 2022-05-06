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
	
	public int getRenterID() {
		return this.renterID;
	}
	
	//Methods for setting optional fields
	public void setBudget(QueryExecutor qe, double budgetLimit) {
		Connection conn = qe.getConnection();
		
		String update = "UPDATE `renter` SET budget = ? WHERE renter_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(update)) {
			
			statement.setDouble(1, budgetLimit);
			statement.setInt(2, this.renterID);
			
			conn.setAutoCommit(false);
			statement.execute();
			
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
	
	public void setLaundry(QueryExecutor qe, String hasLaundry) {
		Connection conn = qe.getConnection();
		
		String update = "UPDATE `renter` SET laundry = ? WHERE renter_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(update)) {
			
			statement.setString(1, hasLaundry);
			statement.setInt(2, this.renterID);
			
			conn.setAutoCommit(false);
			statement.execute();
			
			conn.commit();
			conn.setAutoCommit(true);
			
			//Only update class instance if transaction succeeds
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
	
	public void setFurnishing(QueryExecutor qe, String isFurnished) {
		Connection conn = qe.getConnection();
		
		String update = "UPDATE `renter` SET furnished = ? WHERE renter_id = ?;";
		
		try (PreparedStatement statement = conn.prepareStatement(update)) {
			
			statement.setString(1, isFurnished);
			statement.setInt(2, this.renterID);
			
			conn.setAutoCommit(false);
			statement.execute();
			
			conn.commit();
			conn.setAutoCommit(true);
			
			//Only update class instance if transaction succeeds
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
	 * If any of the optional filters is not specified, they won't be added to the query.
	 * 
	 * Note that the query will set all other filters (city, state, date, etc) to match
	 * the fields initially associated with a renter by default. These fields are automatically
	 * required without the renter explicitly specifying them.
	 * 
	 * Returned property(s) must:
	 * 
	 * - Be in the same city as the renter's city
	 * - Be in the same state as the renter's state
	 * - Have at least the number of bedrooms specified by the renter
	 * - Have at least the number of bathrooms specified by the enter
	 * - Have an available date exactly or before the renter's move-in date
	 * - Not require a minimum stay greater than the renter's maximum stay 
	 * - OR: Not require a maximum stay smaller than the renter's minimum stay 
	 * - Be ordered my most recent available dates
	 * 
	 * Unless the renter explicitly specifies laundry, furnishing, or budget, all
	 * optional fields are considered as "flexible" and are not added to the query
	 * 
	 */
	public void searchAllPropsBy(QueryExecutor qe, boolean byBudget, boolean byLaundry,
			boolean byFurnished) {
		
		Connection conn = qe.getConnection();
		
		String searchQuery = "SELECT * FROM `property` WHERE city LIKE ? AND state LIKE ? "
				+ "AND num_bedrooms >= ? AND num_bathrooms >= ? AND DATE_FORMAT(date_available, '%Y-%m-%d') <= ? "
				+ "AND ((NOT min_stay > ?) OR (NOT max_stay < ?)) AND rent_status = 'Available' "
				+ "ORDER BY date_available";
		
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
			statement.setInt(6, this.minStay);
			statement.setInt(7, this.maxStay);
			
			conn.setAutoCommit(false);
			ResultSet resultSet = statement.executeQuery();
			
			conn.commit();
			conn.setAutoCommit(true);
			
			System.out.println("Query:" + statement);
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
	 * - Increment the number of sent inquiries of the renter by 1
	 * - Increment the number of received inquiries of the owner by 1
	 * - Add a new record of the inquiry using the renter's ID and the property's ID to table "inquiry"
	 * 
	 * @param qe the query executor object
	 * @param prop the property which the renter inquires
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
	
	public static void signContract(QueryExecutor qe) {
		//Updated action status to be "Rented", add to  LEASE table with "Unpaid" status
		
	}
	
	public static void payRent(QueryExecutor qe) {
		
	}
	
	public static void bookmarkFavoriteProps(QueryExecutor qe) {
		
	}
}
