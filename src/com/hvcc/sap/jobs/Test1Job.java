package com.hvcc.sap.jobs;

import java.io.InputStream;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hvcc.sap.util.DateUtils;

public class Test1Job implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Date now = new Date();
		System.out.println("Test1 Job started : " + DateUtils.format(now, "yyyy-MM-dd HH:mm:SS"));
		
		InputStream is = getClass().getResourceAsStream("/resources/sap.config.properties");
	    java.util.Properties props = new java.util.Properties();
	    try {
	    	props.load(is);
	    	String sid = (String)props.get("sap.sid");
	    	System.out.println("sid : " + sid);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

}
