import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ConnectorOrganizer {
	
		   private static ConnectorOrganizer instance = null;
		   protected ConnectorOrganizer() {
		   }
		   public static ConnectorOrganizer getInstance() {
		      if(instance == null) {
		         instance = new ConnectorOrganizer();
		      }
		      return instance;
		   }
		   
		   // Returns a list of Connections. The first connection is the database warehouse connection. The other connections
		   // are connections to instances databases
		   public List<Connection> inititateConnectionProcess() throws SQLException {
			  
			   setUp();
			   List<Connection> connections = new LinkedList<Connection>();
			   
			   
			   // Creates the database warehouse connection and adds it to the top of the list of connections
			   Connection dbWarehouseConnection = createIndividualConnection(getDBWarehouseInfo());
			   connections.add(dbWarehouseConnection);
			   
			   
			   // Creates an iterator with connection info for each instance
			   Iterator<List<String>> connectionInfoItr = getInstanceConnectionInfoList(dbWarehouseConnection).iterator();
			   
			   
			   // Iterates through the iterator and call for a connection to be made using the connection info.
			   while(connectionInfoItr.hasNext()){
				   
				   connections.add(createIndividualConnection(connectionInfoItr.next()));
			   }
			   
			   System.out.println("All database connections attempted");
			   
			   return connections;
			   
		   } 
		 
		   
		   public Connection createIndividualConnection(List<String> singleConnectionInfo) throws SQLException {
			   
			   Connector testConnection = new Connector(singleConnectionInfo);
			   
			  return testConnection.createConnection();
		   }
		   
		  
		   // Returns a list of list of strings. The low level list hold the information needed to connect to one database
		   // and the list of list represents the complete all the database connection info.
		   public List<List<String>> getInstanceConnectionInfoList(Connection dbWarehouseConnection) throws SQLException{
			   
			   // This SQL statement grabs the info that is used make a database connection from job table in the database warehouse
			   String query = "SELECT * FROM demo_connections";
			   
			   List<List<String>> instancesConnectionInfo = new LinkedList<List<String>>();//List of Data bases' lower list has that databases info
			   
			   ResultSet connectionResultSet = null;
			 
			   // Grabs job info from the database warehouse and puts it into a result set.
			   try{
					System.out.println(dbWarehouseConnection.getCatalog());//NOT IN DAVES!!
					if (query != null && query != ""){
						Statement selectStatement = dbWarehouseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
								ResultSet.CONCUR_READ_ONLY);
						
						connectionResultSet = selectStatement.executeQuery(query);
						
					}
				
				}catch ( Exception e ) {
			    	   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			    	   System.exit(0);
				}
				
			   	// Takes the info from the result set and places it into a list.
				while (connectionResultSet.next()) {
					
					List <String> singleInstanceInfo = new LinkedList<String>();
					//Drops data in each cell, to end of column then starts @ 1 lower row at column 0
					for (int i = 1; i< connectionResultSet.getMetaData().getColumnCount() +1 ; i++) {
						singleInstanceInfo.add(connectionResultSet.getString(i));
					}	   
					instancesConnectionInfo.add(singleInstanceInfo);
				}
			   
			   return instancesConnectionInfo;
			   
		   }
		   
		   
		   // Sets up the postgres driver, truststore and keystore
		   public void setUp() {
			   try {

					Class.forName("org.postgresql.Driver");

				} 
			   catch (ClassNotFoundException e) {

					System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
					e.printStackTrace();
					return;
					
				}
			   
			   	String trustStore = System.getProperty("javax.net.ssl.keyStore");
		        if (trustStore == null) {
		            System.out.println("javax.net.ssl.trustStore is not defined");
		        } else {
		            System.out.println("javax.net.ssl.trustStore = " + trustStore);
		        }
		        
			   	System.setProperty("javax.net.ssl.keyStore", "files/myKeyStore.jks");//server.key & server.crt
			    System.setProperty("javax.net.ssl.trustStore", "files/cacerts");// root.crt
				System.setProperty("javax.net.ssl.keyStorePassword", "changeit");// pw
				
				System.out.println("Set up Complete");
			   
		   }
		   
		   // Information for setting up the database warehouse connection
		   public List<String> getDBWarehouseInfo(){
			   List<String> dBWarehouseConnectionInfo = new LinkedList<String>();
			   //dBWarehouseConnectionInfo.add("iniesta08.marwatch.net"); Dave's
			   dBWarehouseConnectionInfo.add("executor.marwatch.net");
			   dBWarehouseConnectionInfo.add("Test");
			   dBWarehouseConnectionInfo.add("mw5");
			   return dBWarehouseConnectionInfo;
		   }


}
