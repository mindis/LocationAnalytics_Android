package com.example.locationanalytics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {
	
	private static DatabaseConnection instance = null;
	Connection conn; 
	public DatabaseConnection(){
		
	}	
	public static DatabaseConnection getInstance(){
		if(instance == null){
			instance = new DatabaseConnection();
			return instance;
		}
		return instance;
	}
	
	 	
	public Connection setConnection(){  
		try{ 
			
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(
			   "jdbc:postgresql://localhost:5432/locationAnalyticsManager","postgres", "password");
//			connection.close();
			System.out.println(conn);
			if(!conn.isClosed()){
				System.out.println("Successfully Connected to " + "PostGres server using TCP/IP");
			} 
		}catch(Exception e){ 
			System.out.println("Could not connect to the PostGres database.");
		  e.printStackTrace();
		}  
		return conn;  
	}  
	
//	public void testConnection() {
//		Statement stmt = conn.createStatement();
//		stmt.executeQuery(sql)
//	}

}


