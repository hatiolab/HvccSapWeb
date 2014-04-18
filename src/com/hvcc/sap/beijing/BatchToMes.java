package com.hvcc.sap.beijing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hvcc.sap.MesUpdater;
import com.hvcc.sap.RfcSearcher;

public class BatchToMes {
	private static final Logger LOGGER = Logger.getLogger(BatchToMes.class.getName());
	public static final String INSERT_BATCH_SQL = "INSERT INTO INF_SAP_BATCH(IFSEQ,WERKS,MATNR,CHARG,ATWTB,LVORM,ERDAT,ERZET,ERNAM,AEDAT,AEZET,AENAM,IFRESULT,IFFMSG,MES_STAT,MES_ISTDT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
	public static final String RFC_FUNC_NAME = "ZRFC_PPG_BATCH_MASTER";
	public static final String RFC_OUT_TABLE1 = "ET_MAT";
	
	/**
	 * RFC function call
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> callRfc(String fromDateStr, String toDateStr) throws Exception {
		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put("IV_WERKS", "GT10");
		inputParams.put("IV_FDATE", fromDateStr);
		inputParams.put("IV_TDATE", toDateStr);
		// 처음 요청일 경우 blank, 재전송 요청일 경우 'X'
		inputParams.put("IV_CHECK", "");

		List<String> outputParams = new ArrayList<String>();
		outputParams.add("EV_IFRESULT");
		outputParams.add("EV_IFMSG");
		
		LOGGER.info("RFC [" + RFC_FUNC_NAME + "] Call!");
		
		String[] outTables = new String[2];
		outTables[0] = RFC_OUT_TABLE1;
		Map<String, Object> output = new RfcSearcher().callFunction(RFC_FUNC_NAME, inputParams, outputParams, outTables);
		return output;
	}
	
	/**
	 * create product data
	 * 
	 * @param results
	 * @throws Exception
	 */
	public int createBatchData(List<Map<String, Object>> results) throws Exception {
		List<List<Object>> parameters = new ArrayList<List<Object>>();
		int resultCnt = results.size();
		for(int i = 0 ; i < resultCnt ; i++) {
			Map<String, Object> record = (Map<String, Object>)results.get(i);
			this.showMap(record);
			List<Object> parameter = new ArrayList<Object>();
			parameter.add(record.get("IFSEQ"));
			parameter.add(record.get("WERKS"));
			parameter.add(record.get("MATNR"));
			parameter.add(record.get("CHARG"));
			parameter.add(record.get("ATWTB"));
			parameter.add(record.get("LVORM"));
			parameter.add(record.get("ERDAT"));
			parameter.add(record.get("ERZET"));
			parameter.add(record.get("ERNAM"));
			parameter.add(record.get("AEDAT"));
			parameter.add(record.get("AEZET"));
			parameter.add(record.get("AENAM"));
			parameter.add(record.get("IFRESULT"));
			parameter.add(record.get("IFMSG"));
			parameter.add("N");
			parameters.add(parameter);
		}

		return new MesUpdater().update(INSERT_BATCH_SQL, parameters);
	}
	
	/**
	 * 실행 
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void execute(String fromDateStr, String toDateStr) {
		Map<String, Object> output = null;
		int resultCount = 0;

		try {
			output = this.callRfc(fromDateStr, toDateStr);
			String ifresult = output.get("EV_IFRESULT").toString();
			if("S".equals(ifresult)) {
				List<Map<String, Object>> matResults = (List<Map<String, Object>>)output.get(RFC_OUT_TABLE1);
				resultCount = this.createBatchData(matResults);
				LOGGER.info("Got (" + resultCount + ") Batch From SAP!");
			} else {
				LOGGER.info("Failed to get Batch From SAP!");
			}			
		} catch (Exception e) {
			LOGGER.severe("Failed to get Batch From SAP! " + e.getMessage());
		}	
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
		LOGGER.info(buf.toString());
	}
	
	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date fromDate = new Date();
        String fromDateStr = df.format(fromDate);
        Calendar c = Calendar.getInstance(); 
        c.setTime(fromDate);
        c.add(Calendar.DATE, 1);
        Date toDate = c.getTime();
        String toDateStr = df.format(toDate);		
		new BomToMes().execute(fromDateStr, toDateStr);
	}
}
