package edu.mssm.icahn.playground;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HiveTest {

	/**
	 * Just a demo Class to test the Hive access...
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {		
//		Class.forName("org.apache.hive.jdbc.HiveDriver");
		
//		hive --service hiveserver2 &
		Console con=System.console();
        System.out.println("Type password");
        char [] str=con.readPassword();

        Class.forName("org.apache.hive.jdbc.HiveDriver");
//    	Connection conn = DriverManager.getConnection("jdbc:hive2://localhost:10000/proj_varimpact", "xthomap01", new String(str));
        Connection conn = DriverManager.getConnection("jdbc:hive2://demeter-login1.demeter.hpc.mssm.edu/proj_varimpact", "xthomap01", new String(str));
    	
		Statement stmt = conn.createStatement(); 
		ResultSet rs = stmt.executeQuery("SELECT * FROM genes WHERE  pmid = 18639367 AND species = 9606");

		while(rs.next()){
			int a = rs.getInt("confidence");
			int b = rs.getInt("species");
			int c = rs.getInt("begingene");
			int d = rs.getInt("endgene");
			String e = rs.getString("entity");
			
			System.out.println(a);
			System.out.println(b);
			System.out.println(c);
			System.out.println(d);
			System.out.println(e);
		}
		
		
	}

}
