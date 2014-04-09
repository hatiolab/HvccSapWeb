package com.hvcc.sap.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hvcc.sap.beijing.ScrapToSap;

public class ScrapToSapJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ScrapToSap scrapToSap = new ScrapToSap();
		scrapToSap.execute();
	}

}
