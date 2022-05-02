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
			statement.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				System.out.print("Transaction is being rolled back");
				conn.rollback();
			} catch (SQLException e2) {
				qe.handleSQLException(e2);
			}
		}
	}
	
	public void createProperty(QueryExecutor qe, Property prop) {
		Connection conn = qe.getConnection();
		
		String query = "INSERT INTO `property` (prop_id, owner_id, address, city, "
				+ "state, monthly_rent, laundry, furnished, num_bedrooms,"
				+ "num_bathrooms, date_available, min_stay, max_stay, rent_status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
		try (PreparedStatement statement = conn.prepareStatement(query)) {
			
			statement.setInt(1, prop.getPropID());
			statement.setInt(2, this.ownerID);
			statement.setString(3, prop.getPropAddress());
			statement.setString(4, prop.getPropCity());
			statement.setString(5, prop.getPropState());
			statement.setDouble(6, prop.getPropRent());
			statement.setBoolean(7, prop.getLaundry());
			statement.setBoolean(8, prop.getFurnished());
			statement.setInt(9, prop.getNumBedroom());
			statement.setDouble(10, prop.getNumBathroom());
			statement.setDate(11, prop.getDateAvailable());
			statement.setInt(12, prop.getMinStay());
			statement.setInt(13, prop.getMaxStay());
			statement.setString(14, prop.getPropStatus());
			
			conn.setAutoCommit(false);
			statement.execute();
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				System.out.print("Transaction is being rolled back");
				conn.rollback();
			} catch (SQLException e2) {
				qe.handleSQLException(e2);
			}
		}
	}
	
	public static void acceptInquiry(QueryExecutor qe) {
		// only accept request if Renter's status is not "Rented"
	}
	
	public static void sendLeaseContract(QueryExecutor qe) {
		
	}
	
	public static void acceptRentPayment(QueryExecutor qe) {
		
	}
	
	public static void main(String[] args) {
		QueryExecutor qe = new QueryExecutor();
		

	
	}

	
}
