import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobOrganizer {

		private List<Connection> instancesConnections = new LinkedList<Connection>();
		private Connection dbWarehouseConnection = null;
		private List<List<String>> listOfJobs = new LinkedList<List<String>>();
		
		private static JobOrganizer instance = null;
		protected JobOrganizer() {
		}
		public static JobOrganizer getInstance() {
		    if(instance == null) {
		       instance = new JobOrganizer();
		    }
		    return instance;
		}
		
		public void initialize(List<Connection> connections) throws SQLException {
			
			
			// The first connection in the incoming list is always the DB warehouse connection.  This is
			// placed into its own connection variable and the list of connection then only contains the connections
			// of the instances that is to be read from
			
			instancesConnections = connections;
			dbWarehouseConnection = instancesConnections.get(0);
			instancesConnections.remove(0);
	
			listOfJobs = getListOfJobs();
		
		}
		
		
		// Schedules jobs to be run based on the frequnecy found in the job list.
		public void scheduleJobs() throws SQLException{
			
			final int THREAD_POOL_SIZE = 5; //limit how many jobs its running @ once, to presever cpu power, get lower
			final int JOB_VARIABLE_SIZE = 4; //how many columns in job 
			final int JOB_FREQUENCY_INDEX = 3; //
			
			ListIterator<List<String>> jobItr = listOfJobs.listIterator();
			ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
			
			while(jobItr.hasNext()){
				List<String> currentJob = jobItr.next();
				
				
				//This is the default job frequency should the frequency not be stated. Default frequency is an hour
				int jobFrequency  = 1;
				
				if (currentJob.size() == JOB_VARIABLE_SIZE ){
					try{
						jobFrequency = Integer.parseInt(currentJob.get(JOB_FREQUENCY_INDEX));
						
					}catch ( Exception e ) {
				    	   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
				    	   System.exit(0);
					}
				}
				
				Job job = new Job(dbWarehouseConnection, currentJob, instancesConnections);
				scheduler.scheduleAtFixedRate(job, 0,jobFrequency, TimeUnit.MINUTES);
			}
			System.out.println("All jobs scheduled");
		}

		
		// Returns a list of list of strings. The low level list represents a single job and the list of list represents the complete list of jobs
		public List<List<String>> getListOfJobs() throws SQLException{
			
			
			// This SQL statement grabs the info that is used to define a job from the job table in the database warehouse
			String sqlStatement = "SELECT * FROM public.demo_jobs";
			
			List<List<String>> jobList = new LinkedList<List<String>>();
			ResultSet report = null;
			
			
			// Grabs job info from the database warehouse and puts it into a result set.
			try{
				
				System.out.println(dbWarehouseConnection.getCatalog());
				
					Statement selectStatement = dbWarehouseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
							ResultSet.CONCUR_READ_ONLY);
					
					report = selectStatement.executeQuery(sqlStatement);
			
			}catch ( Exception e ) {
		    	   System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		    	   System.exit(0);
			}
			
			
			// Takes the info from the result set and places it into a list.
			while (report.next()) {
				
				List<String> job = new LinkedList<String>();
				for (int i = 1; i< report.getMetaData().getColumnCount() +1 ; i++) {
					job.add(report.getString(i));
				}	   
				jobList.add(job);
			}
			System.out.println("List of jobs fetched");
			return jobList;
			
		}
	
}
