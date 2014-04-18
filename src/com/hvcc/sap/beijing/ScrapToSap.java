/**
 * 
 */
package com.hvcc.sap.beijing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hvcc.sap.MesSearcher;
import com.hvcc.sap.MesUpdater;
import com.hvcc.sap.RfcInvoker;

/**
 * Scrap Interface : MES --> SAP
 * 
 * @author Shortstop
 */
public class ScrapToSap {

	/**
	 * logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ScrapToSap.class.getName());
	/**
	 * RFC Function Name
	 */
	public static final String RFC_FUNC_NAME = "ZPPG_EA_INLINE_SCRAP";
	
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
		Map<String, Object> output = new RfcInvoker().callFunction(RFC_FUNC_NAME, "IS_SCRAP", inputParams, outputParams);
		return output;
	}
	
	/**
	 * Select from MES Scrap Table
	 * 
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectScraps() throws Exception {
		String sql = "SELECT MES_ID, IFSEQ, WERKS, ARBPL, EQUNR, LOGRP, VAART, ZVAART, MATNR, IDNRK, BUDAT, PDDAT, ERFMG, MEINS FROM INF_SAP_SCRAP WHERE IFRESULT = 'N'";
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
	public boolean updateStatus(String mesId, String status, String msg) throws Exception {
		String preparedSql = "UPDATE INF_SAP_SCRAP SET IFRESULT = ?, IFFMSG = ? WHERE MES_ID = ?";
		List parameters = new ArrayList();
		List param = new ArrayList();
		param.add(status);
		param.add(msg);
		param.add(mesId);
		parameters.add(param);
		int result = new MesUpdater().update(preparedSql, parameters);
		return result > 0;
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
					
					if(output != null && output.containsKey("EV_IFSEQ")) {
						this.info("Scrap result (EV_IFSEQ) : " + output.get("EV_IFSEQ").toString());
					}					
				}
			} else {
				this.info("No scrap data to interface!");
			}
		} catch (Exception ex) {
			LOGGER.severe(ex.getMessage());
		}	
	}
	
	private Map<String, Object> executeRecord(String mesId, Map<String, Object> inputParam) throws Exception {
		this.showMap(inputParam);
		Map<String, Object> output = null;
		
		try {
			output = this.callRfc(inputParam);
			this.updateStatus(mesId, "Y", null);
		} catch (Throwable th) {
			String msg = "Error - MES_ID : " + mesId + ", MSG : " + th.getMessage();
			LOGGER.severe(msg);
			
			if(msg.length() > 250) 
				msg = msg.substring(0, 250);
			this.updateStatus(mesId, "E", msg);
		}
		
		return output;
	}
	
	private void info(String msg) {
		LOGGER.info(msg);
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
		new ScrapToSap().execute();
	}
}
