package com.hvcc.sap.jobs;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hvcc.sap.MesConnectionFactory;
import com.hvcc.sap.util.DateUtils;

public class Test2Job implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Date now = new Date();
		System.out.println("Test2 Job started : " + DateUtils.format(now, "yyyy-MM-dd HH:mm:SS"));
		
		try {
			Connection conn = MesConnectionFactory.getInstance().getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) cnt from users");
			while(rs.next()) {
				System.out.println("count : " + rs.getInt(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		InputStream is = getClass().getResourceAsStream("/resources/sap.config.properties");
	    java.util.Properties props = new java.util.Properties();
	    try {
	    	props.load(is);
	    	String sid = props.getProperty("sap.sid");
	    	String maxCon = props.getProperty("sap.max_con");
	    	String client = props.getProperty("sap.client");
	    	String user = props.getProperty("sap.user");
	    	String password = props.getProperty("sap.password");
	    	String ip = props.getProperty("sap.ip");
	    	String system = props.getProperty("sap.system");
	    	
	    	System.out.println("sid : " + sid);
	    	System.out.println("max_con : " + maxCon);
	    	System.out.println("client : " + client);
	    	System.out.println("user : " + user);
	    	System.out.println("password : " + password);
	    	System.out.println("ip : " + ip);
	    	System.out.println("system : " + system);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }		
	}

}
