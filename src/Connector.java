
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class Connector {
	
	private String hostName, databaseName, user;
	final int NUMBER_OF_CONNECTION_PARAMETERS = 3;
	
	public Connector(List <String> singleConnectionInfo){
		
		if (singleConnectionInfo.size()== NUMBER_OF_CONNECTION_PARAMETERS)
		{
			hostName = singleConnectionInfo.get(0);
			databaseName = singleConnectionInfo.get(1);
			user = singleConnectionInfo.get(2);
		}
		else{
			hostName = null;
			databaseName = null;
			user = null;
		}
		
	}
	
	// creates a jdbc connection to a database that is specified by the database info. Returns the connection created. SSL is used.
	public Connection createConnection() throws SQLException{
		
			System.out.println("PostgreSQL JDBC Driver Registered");
			Connection connection = null;
		
			try {
				
				String url = "jdbc:postgresql://" + hostName +":" + "5432" + "/" + databaseName;
						
				Properties props = new Properties();
				props.setProperty("user",user);
				props.setProperty("ssl","true");
				connection = DriverManager.getConnection(url,props);
				
				System.out.println("Attempting to make JDBC conncetion to " + url);
	
				} catch (SQLException e) {
					
				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
				return null;
	
				}
	
				if (connection != null) {
	
				System.out.println("Connection successful!");
				return connection;
	
				} else {
	
				System.out.println("Failed to make connection!");
				return null;
	
				}

	}

}
