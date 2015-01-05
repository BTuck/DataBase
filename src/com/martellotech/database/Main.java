package com.martellotech.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws SQLException {

		List<Connection> connections = ConnectorOrganizer.getInstance().inititateConnectionProcess();
		// TODO win
		JobOrganizer jobOrganizer = JobOrganizer.getInstance();
		jobOrganizer.initialize(connections);
		System.out.println("Connections made Ben");

		jobOrganizer.scheduleJobs();
	}

}
