import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Job implements Runnable {
	
	private Connection dbWarehouseConnection;
	private List<String> currentJob = new LinkedList<String>();
	private List<Connection> instancesConnections = new LinkedList<Connection>();
	
	public Job(Connection dbWarehouseConnection, List<String> currentJob, List<Connection> instancesConnections ){
		this.dbWarehouseConnection = dbWarehouseConnection;
		this.currentJob = currentJob;
		this.instancesConnections = instancesConnections;
	}
	
	@Override
	public void run(){
		ListIterator<Connection> connectionItr = instancesConnections.listIterator();
		
		while (connectionItr.hasNext()) {
		
			Connection currentConnction = connectionItr.next();
			
			try {
				insertData(selectData(currentJob.get(0), currentConnction ), currentJob.get(1), 
						currentConnction.getCatalog() );
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
		}
		
		try {
			runScript(currentJob.get(2));
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	// Selects the the data from a table in an instances database that is specified in the job list
	public ResultSet selectData(String query, Connection currentConnection) throws SQLException{
		
		ResultSet report = null;
		
		try{
			System.out.println("Selecting data from " + currentConnection.getCatalog());
			if (query != null && query != ""){
				Statement selectStatement = currentConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
				
				report = selectStatement.executeQuery(query);
				
			}
		
		}catch ( Exception e ) {
	    	   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	    	   System.exit(0);
		}
	
					
		return report;
	}
	
	// Inserts the data which is stored in a result set and places it in the specified table
	public void insertData(ResultSet report, String tableName, String instanceName) throws SQLException{
		
		if(tableName != null && tableName != "" && report!= null) {
			
			Statement insertStatement =  dbWarehouseConnection.createStatement();
		
			ResultSetMetaData reportMetaData = report.getMetaData();
			
			String columnNames = getColumnNames(tableName);
			
			String query = "INSERT INTO " + tableName + " (" + columnNames + ") Values(";
			
			report.first();
			
				try {
					
					while (report.next()) {
						
						query = query + "\'" + instanceName + "\', CURRENT_TIMESTAMP,";
		
						for (int i = 1; i< reportMetaData.getColumnCount() +1 ; i++) {
							if (i<reportMetaData.getColumnCount())
								query =  query + "\'" + report.getString(i) + "\', ";
							else {
								query =  query + "\'" + report.getString(i) + "\'), (";
							}
					 
						}	   
			
					}
				
				query = query.substring(0, query.length() - 3);

				insertStatement.executeUpdate(query);
			       
		       	} catch ( Exception e ) {
		    	  System.err.println( e.getClass().getName()+": "+ e.getMessage());
		    	  System.exit(0);
			
		       	}
				System.out.println("Inserting data into table " + tableName);
			}
	}
	
	// Runs whatever script that is included in the job
	public void runScript(String script) throws SQLException {
		
		if (script != null && script != "") {
		
			Statement scriptSQL =  dbWarehouseConnection.createStatement();
			scriptSQL.executeUpdate(script);
			
			System.out.println("Executing Script");
		}
		
		System.out.println("Job Complete");
	}
	
	// Gets the column names of the table specified in the database warehouse
	public String getColumnNames(String tableName) throws SQLException {
		Statement insertStatement =  dbWarehouseConnection.createStatement();
		String query = "SELECT * FROM " + tableName;
		
		ResultSetMetaData metaData = insertStatement.executeQuery(query).getMetaData();
		String columnNames = "";
		
		   for (int i = 1; i< metaData.getColumnCount() +1 ; i++) {
				  
			   columnNames = columnNames + metaData.getColumnName(i) + ", ";
				  
		   }	
		  
		  return  columnNames = columnNames.substring(0, columnNames.length() - 2);
	}
	
}
