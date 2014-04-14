/**
 * 
 */
package com.hvcc.sap;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * JDBC Connection
 * 
 * @author Shortstop
 */
public class JdbcConnection implements IMesConnection {
	
	public static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String URL = "jdbc:oracle:thin:@172.20.9.1:1521:HMES";
	public static final String USER = "HVCCB_MES";
	public static final String PASSWORD = "HVCCB_MES";
	
	@Override
	public Connection getConnection() throws Exception {
		Class.forName(DRIVER).newInstance();
		Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
		return conn;
	}

}
