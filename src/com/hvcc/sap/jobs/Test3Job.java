package com.hvcc.sap.jobs;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hvcc.sap.util.DateUtils;

public class Test3Job implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {		
		Date now = new Date();
		System.out.println("Test3 Job started : " + DateUtils.format(now, "yyyy-MM-dd HH:mm:SS"));
	}
}
