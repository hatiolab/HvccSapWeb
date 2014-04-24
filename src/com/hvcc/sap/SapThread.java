package com.hvcc.sap;

import java.util.Date;
import java.util.logging.Logger;

import com.hvcc.sap.beijing.ActualToSap;
import com.hvcc.sap.beijing.ProductToMes;
import com.hvcc.sap.beijing.ParameterToMes;
import com.hvcc.sap.beijing.PlanToMes;
import com.hvcc.sap.beijing.ScrapToSap;
import com.hvcc.sap.util.DateUtils;

public class SapThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(SapThread.class.getName());
	private boolean running = true;
	private int count = 0;
	
	@Override
	public void run() {
		
		while(running) {
			this.sleep(Constants.EXE_INTERVAL);
			
			count++;

			if(this.checkSap()) {
				this.ifcActual();
				this.ifcScrap();
			
				if(count % 10 == 0) {
					count = 0;
					this.ifcPlan();
					this.ifcProduct();
					this.ifcParameter();
				}
			}
		}
	}
	
	private void sleep(long millisecond) {
		try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
		}
	}
	
	private boolean checkSap() {
		try {
			return SapConnectionPool.getInstance().isAlive();
		} catch(Throwable th) {
			return false;
		}
	}
	
	private void ifcActual() {
		LOGGER.info("Actual ....");
		ActualToSap actual = new ActualToSap();
		actual.execute();
	}
	
	private void ifcScrap() {
		this.sleep(Constants.EXE_RFC_INTERVAL);
		LOGGER.info("Scrap ....");
		ScrapToSap actual = new ScrapToSap();
		actual.execute();
	}

	private void ifcPlan() {
		this.sleep(Constants.EXE_RFC_INTERVAL);
		LOGGER.info("Plan ....");
		String[] dateInfo = this.getDateInfo();
		PlanToMes plan = new PlanToMes();
		plan.execute("", dateInfo[0], dateInfo[1]);
	}
	
	private void ifcProduct() {
		this.sleep(Constants.EXE_RFC_INTERVAL);
		LOGGER.info("Product ....");
		String[] dateInfo = this.getDateInfo();
        ProductToMes productToMes = new ProductToMes();
        productToMes.execute("", dateInfo[0], dateInfo[1]);
	}
	
	private void ifcParameter() {
		this.sleep(Constants.EXE_RFC_INTERVAL);
		LOGGER.info("Parameter ....");
		String[] dateInfo = this.getDateInfo();
        ParameterToMes paramToMes = new ParameterToMes();
        paramToMes.execute("", dateInfo[0], dateInfo[1]);
	}
	
	private String[] getDateInfo() {
		Date fromDate = new Date();
        String fromDateStr = DateUtils.format(fromDate, Constants.SAP_DATEFORMAT);
        String toDateStr = DateUtils.format(fromDate, Constants.SAP_DATEFORMAT);
        String[] str = new String[2];
        str[0] = fromDateStr;
        str[1] = toDateStr;
        return str;
	}
}
