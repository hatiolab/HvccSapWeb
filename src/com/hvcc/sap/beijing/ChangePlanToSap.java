/**
 * 
 */
package com.hvcc.sap.beijing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.hvcc.sap.MesSearcher;
import com.hvcc.sap.MesUpdater;
import com.hvcc.sap.RfcInvoker;

/**
 * TODO
 * 계획 변경시 MES에서 SAP로 보낸다.
 * 
 * @author Shortstop
 */
public class ChangePlanToSap {
	
	private static final Logger LOGGER = Logger.getLogger(ChangePlanToSap.class.getName());
	public static final String RFC_FUNC_NAME = "ZPPG_EA_CHANGE_PLAN";
	
	/**
	 * call rfc 
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> callRfc(Map<String, Object> inputParams) throws Exception {
		List<String> outputParams = new ArrayList<String>();
		outputParams.add("ES_RESULT");
		outputParams.add("EV_IFSEQ");
		
		LOGGER.info("RFC [" + RFC_FUNC_NAME + "] Call!");
		Map<String, Object> output = new RfcInvoker().callFunction(RFC_FUNC_NAME, "IT_PLAN", inputParams, outputParams);
		return output;
	}
	
	/**
	 * Select from MES Scrap Table
	 * 
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectScraps() throws Exception {
		String sql = "SELECT MES_ID, IFSEQ, WERKS, ARBPL, EQUNR, TRIM(LOGRP) LOGRP, VAART, ZVAART, MATNR, IDNRK, BUDAT, PDDAT, ERFMG FROM INF_SAP_SCRAP WHERE IFRESULT = 'N'";
		return new MesSearcher().search(sql);
	}
	
	/**
	 * Update status flag MES Scrap table
	 * 
	 * @param mesId
	 * @param status
	 * @return 
	 * @throws Exception
	 */
	public boolean updateStatus(String mesId, String status) throws Exception {
		String sql = "UPDATE INF_SAP_SCRAP SET MES_STAT = '" + status + "' WHERE MES_ID = '" + mesId + "'";
		return new MesUpdater().update(sql);
	}
	
	/**
	 * 실행 
	 * 
	 * @throws Exception
	 */
	public void execute() {
		try {
			List<Map<String, Object>> scraps = this.selectScraps();
			if(!scraps.isEmpty()) {
				int scrapCount = scraps.size();
				for(int i = 0 ; i < scrapCount ; i++) {
					Map<String, Object> inputParam = scraps.get(i);
					String mesId = (String)inputParam.remove("MES_ID");
					Map<String, Object> output = this.executeRecord(mesId, inputParam);
					this.info(output.get("EV_IFSEQ").toString());
				}
			} else {
				this.info("No scrap data to interface!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.log(Level.SEVERE, null, ex);
		}	
	}
	
	private Map<String, Object> executeRecord(String mesId, Map<String, Object> inputParam) throws Exception {
		this.showMap(inputParam);
		Map<String, Object> output = null;
		
		try {
			output = this.callRfc(inputParam);
			this.updateStatus(mesId, "Y");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, "Error - MES_ID : " + mesId + ", MSG : " + e.getMessage());
			this.updateStatus(mesId, "E");
		}
		
		return output;
	}
	
	private void info(String msg) {
		LOGGER.info(msg);
		//System.out.println(msg);
	}
	
	@SuppressWarnings("rawtypes")
	private void showMap(Map map) {
		StringBuffer buf = new StringBuffer();
		Iterator iter = map.keySet().iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			String value = (map.get(key) == null ? "" : map.get(key).toString());
			buf.append(key);
			buf.append(" : ");
			buf.append(value);
			buf.append(", ");
		}
		this.info(buf.toString());
	}
	
	public static void main(String[] args) {
		new ChangePlanToSap().execute();
	}
}
