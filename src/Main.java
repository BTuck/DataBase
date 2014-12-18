
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws SQLException {

		List <Connection> connections = ConnectorOrganizer.getInstance().inititateConnectionProcess();
		
		JobOrganizer jobOrganizer = JobOrganizer.getInstance();
		jobOrganizer.initialize(connections);		
		System.out.print("Connections made");
		
		jobOrganizer.scheduleJobs();		
	}

}

