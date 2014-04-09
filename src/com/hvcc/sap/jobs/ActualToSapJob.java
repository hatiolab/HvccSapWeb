package com.hvcc.sap.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hvcc.sap.beijing.ActualToSap;

public class ActualToSapJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ActualToSap actualToSap = new ActualToSap();
		actualToSap.execute();
	}

}
