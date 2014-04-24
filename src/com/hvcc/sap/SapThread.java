package com.hvcc.sap;

import java.util.Date;
import java.util.logging.Logger;

import com.hvcc.sap.beijing.ActualToSap;
import com.hvcc.sap.beijing.BatchToMes;
import com.hvcc.sap.beijing.BomToMes;
import com.hvcc.sap.beijing.MachineToMes;
import com.hvcc.sap.beijing.ParameterToMes;
import com.hvcc.sap.beijing.PlanToMes;
import com.hvcc.sap.beijing.ScrapToSap;
import com.hvcc.sap.util.DateUtils;

public class SapThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(SapThread.class.getName());
	private boolean running = true;
	private int count = 1;
	
	@Override
	public void run() {
		
		while(running) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
						
			LOGGER.info("Run...");
			
			count++;
			this.ifcActual();
			this.ifcScrap();
			this.ifcPlan();
			
			// 10분에 한번씩 master
			if(count % 10 == 0) {
				count = 0;
				this.ifcProduct();
				this.ifcBatch();
				this.ifcMachine();
				this.ifcParameter();
			}
		}
	}
	
	private void ifcActual() {
		LOGGER.info("Actual ....");
		ActualToSap actual = new ActualToSap();
		actual.execute();
	}
	
	private void ifcScrap() {
		LOGGER.info("Scrap ....");
		ScrapToSap actual = new ScrapToSap();
		actual.execute();
	}

	private void ifcPlan() {
		LOGGER.info("Plan ....");
		String[] dateInfo = this.getDateInfo();
		PlanToMes plan = new PlanToMes();
		
		LOGGER.info("from date : " + dateInfo[0] + ", to date : " + dateInfo[1]);
		plan.execute(dateInfo[0], dateInfo[1]);
	}
	
	private void ifcProduct() {
		LOGGER.info("Product ....");
		String[] dateInfo = this.getDateInfo();
        BomToMes bomToMes = new BomToMes();
        
        
        bomToMes.execute(dateInfo[0], dateInfo[1]);
	}
	
	private void ifcMachine() {
		LOGGER.info("Machine ....");
		String[] dateInfo = this.getDateInfo();
        MachineToMes machineToMes = new MachineToMes();
        machineToMes.execute(dateInfo[0], dateInfo[1]);
	}
	
	private void ifcParameter() {
		LOGGER.info("Parameter ....");
		String[] dateInfo = this.getDateInfo();
        ParameterToMes param = new ParameterToMes();
        param.execute(dateInfo[0], dateInfo[1]);		
	}
	
	private void ifcBatch() {
		LOGGER.info("Batch ....");
		String[] dateInfo = this.getDateInfo();
        BatchToMes batch = new BatchToMes();
        batch.execute(dateInfo[0], dateInfo[1]);
	}
	
	private String[] getDateInfo() {
		Date fromDate = new Date();
        String fromDateStr = DateUtils.format(fromDate, "yyyyMMdd");
        String toDateStr = DateUtils.format(fromDate, "yyyyMMdd");
        String[] str = new String[2];
        str[0] = fromDateStr;
        str[1] = toDateStr;
        return str;
	}
}
