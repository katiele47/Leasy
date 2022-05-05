package comp378_JDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Renter {
	
	//STATIC 
	
	//required fields
	public int renterID;
	public String first;
	public String last;
	public String city;
	public String state;
	int numBedroom;
	public int numBathroom;
	
	//duaration 
	public String date;
	public int minStay;
	public int maxStay;
	
	//optional fields
	public double budget = 0;
	public boolean laundry = false;
	public boolean furnished = false;
	
	
	//DYNAMIC
	
	//dynamic fields
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
	
	//Methods for setting optional fields
	public void setBudget(double budgetLimit) {
		this.budget = budgetLimit;
	}
	
	public void setLaundry(boolean hasLaundry) {
		this.laundry = hasLaundry;
	}
	
	public void setFurnishment(boolean isFurnished) {
		this.furnished = isFurnished;
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
	 * Search all properties matching optional filters - budget, laundry, and furnished. 
	 * If any of the optional filters is not specified, they won't be added to the query.
	 * 
	 * Note that the query will set all other filters (city, state, date, etc) to match
	 * the fields initially associated with a renter by default. These fields are automatically
	 * added to the query without the renter explicitly specifying them.
	 * 
	 */
	public void searchAllPropsBy(QueryExecutor qe, boolean byBudget, boolean byLaundry,
			boolean byFurnished) {
		
		Connection conn = qe.getConnection();
		
		String searchQuery = "SELECT * FROM `property` WHERE city LIKE ? AND state LIKE ? "
				+ "AND num_bedrooms >= ? AND num_bathrooms >= ? AND DATE_FORMAT(date_available, '%Y-%m-%d') <= ? "
				+ "AND ((NOT min_stay > ?) OR (NOT max_stay < ?)) AND rent_status = 'Available'";
		
		//Optional fields
		if (byBudget && this.budget > 0) {
			searchQuery = searchQuery + " AND monthly_rent <= " + this.budget;
		}
		if (byLaundry && this.laundry) {
			searchQuery = searchQuery + " AND laundry = " + this.laundry;
		}
		if (byFurnished && this.furnished) {
			searchQuery = searchQuery + " AND furnished = " + this.furnished;
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
			
			System.out.println("Query:");
			System.out.println(statement);
			
			System.out.println("Result:");
			String rs1, rs2;
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
	
	
//	public static void searchPropsByBathroom(QueryExecutor qe) {
//		//default: city + state + status "Available"
//		//numBath >= renter's
//	
//	}
//	
//	public static void searchPropsByBudget(QueryExecutor qe) {
//		//default: city + state + status "Available"
//		//numBath >= renter's
//	
//	}
//	
//	public static void searchPropsByDuration(QueryExecutor qe) {
//		//default: city + state + status "Available"
//		//min stay + max stay
//	
//	}
//	
//	public static void searchPropsByFurnishment(QueryExecutor qe) {
//		//default: city + state + status "Available"
//		
//	
//	}
//	
//	public static void searchPropsByLaundry(QueryExecutor qe) {
//		//default: city + state + status "Available"
//		
//	
//	}
//	
//	public static void searchPropsByDateAvailable(QueryExecutor qe) {
//		//default: city + state + status "Available"
//		
//	
//	}
	
	public static void sendInquiry(QueryExecutor qe) {
	
	}
	
	public static void signContract(QueryExecutor qe) {
		//Updated action status to be "Rented", add to  LEASE table with "Unpaid" status
	}
	
	public static void payRent(QueryExecutor qe) {
		
	}
	
	public static void bookmarkFavoriteProps(QueryExecutor qe) {
		
	}
}
