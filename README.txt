DaveOBase System

This part of the DaveOBase system is responsible for only filling up the database warehouse with data. Another system will need to be created 
to actually grab data from the database warehouse. See here for more info including a UML diagram: 
http://jira.martellotech.com:8080/browse/MWV-511

The flow of the system will be described first.

The first step is to make a JDBC connection to the database warehouse. Once this connection is made, data which is used to create the JDBC
connections to the various instances is stored in a list. This data contains the hostname, the database name, and the username to be used to 
log in. SSL certificate authentication is used to establish the connection. For SSL to work, a signed certificate, a key, and a root
certificate need to be placed in a folder such that the postgres database can access it (This is the server side). Note this would have to
be done for each database instance. The DaveOBase (the client side) needs to have access to the root certificate which is stored in the 
cacerts truststore, and a key and signed certificate which is stored in a PKCS12 keystore. All these JDBC connections are then made and 
stored in a list. At the head of this list the connection to the database warehouse exists. For more information on the where to place 
these certificates please read the server and client side configuration of this project: https://github.com/martellotech/Licensing

Jobs are then scheduled in a ScheduledThreadPoolExecuter, which has a thread pool size of five. This makes sure that not too many jobs
are run at one time. A job contains four distinct fields. The first field contains the select statement. This select statement contains 
the SQL instructions of which data is to be grabbed from each instance. The second field contains a table name. This table name indicates 
in which table the data selected from the select statement and collected from each instance should be placed. Note this table always exists
inside the database warehouse. The third field contains a SQL script. This script could contain anything. It will be executed only on the 
database warehouse. The fourth and final field contains the frequency at which this job should be run. The unit of this frequency is
currently in hours and a data type of type integer is accepted. This can be easily changed within the code.

A job does not have to have every field filled in to be run. It should be noted that the first two fields are dependent on each other, 
that is they both need to be correctly filled in if they are to be run. If the frequency field is left blank then a default frequency of 
one hour will be used instead. All these data fields which describe how a job is to be run is stored in a table in the database warehouse. 
The DaveOBase then stores this information in a list, and iterates through this list scheduling these jobs in the 
ScheduledThreadPoolExecuter. 

For additional clarification the classes will be broken down class by class. The Main class is the main class where the entry point exists.
It initializes the ConnectorOrganizer and the JobOrganizer. 

The ConnectorOrganizer is a singleton class responsible for setting up the postgres driver and setting up the path to the truststore and
the keystore. It is responsible for grabbing the data needed to create the connections to the various different instances. The class
itself holds the connection info needed to connect to the database warehouse. It is also responsible for initiating the connection process
to the various databases. It creates a list of connections to the various instances. It loops through the connection info list and creates a 
new Connector Object with this info. 

The Connector class is responsible for creating the actual JDBC connection. It contains a hostname, database name, and user field, which are
initialized in its constructor and then used to construct the url needed to make the connection.

The JobOrganizer is a singleton class. It is responsible for grabbing the job info data from the database warehouse and placing it into a
list. It is responsible for scheduling the jobs from the list into a ScheduledThreadPoolExecutor. It is passed the list of connections by
the Main class. This class creates new jobs from the data in the job list.

The job class implements the Runnable class. This allows it to be scheduled to be run by the ScheduledThreadPoolExecutor. It contains a copy
of the job info need such as the select statement, the table name, and the SQL script. It also contains the dataWarehouse connection as 
well as a connection of a single instance. This class implements the selectData method which grabs data from all the instances based the SQL
select statement. It also contains an insertData method which is used to insert the data collected from the selectData method into the 
specified table in the database warehouse. The runScript method runs a generic script on the database warehouse. 



