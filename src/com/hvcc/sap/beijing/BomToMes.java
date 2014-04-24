/**
 * 
 */
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
import java.util.logging.Level;

import com.hvcc.sap.MesUpdater;
import com.hvcc.sap.RfcSearcher;

/**
 * @author Shortstop
 *
 */
public class BomToMes {
	private static final Logger LOGGER = Logger.getLogger(BomToMes.class.getName());
	public static final String INSERT_PRODUCT_SQL = "INSERT INTO INF_SAP_PRODUCT(IFSEQ, WERKS, MATNR, MAKTX, MTART, MEINS, MATKL, BESKZ, SOBSL, MMSTA, BSTMA, MPQNT, ERDAT, ERZET, ERNAM, AEDAT, AEZET, AENAM, IFRESULT, IFFMSG, MES_STAT, MES_ISTDT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
	public static final String INSERT_BOM_SQL = "INSERT INTO INF_SAP_BOM(IFSEQ, WERKS, MATNR, STLAN, STLAL, IDNRK, MENGE, MEINS, DATUV, DATUB, ERDAT, ERZET, ERNAM, AEDAT, AEZET, AENAM, IFRESULT, IFFMSG, MES_STAT, MES_ISTDT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
	public static final String INSERT_BATCH_SQL = "INSERT INTO INF_SAP_BATCH(IFSEQ,WERKS,MATNR,CHARG,NAME1,LVORM,ERDAT,ERZET,ERNAM,AEDAT,AEZET,AENAM,IFRESULT,IFFMSG,MES_STAT,MES_ISTDT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";
	public static final String RFC_FUNC_NAME = "ZPPG_EA_MAT_BOM_MASTER";
	public static final String RFC_OUT_TABLE1 = "ET_MAT";
	public static final String RFC_OUT_TABLE2 = "ET_BOM";
	public static final String RFC_OUT_TABLE3 = "ET_BAT";
	
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
		inputParams.put("IV_WERKS", "CN10");
		inputParams.put("IV_FDATE", fromDateStr);
		inputParams.put("IV_TDATE", toDateStr);
		// 처음 요청일 경우 blank, 재전송 요청일 경우 'X', blank : 성공한 건 안내려감, X : 무조건 모두 내림
		inputParams.put("IV_CHECK", "X");

		List<String> outputParams = new ArrayList<String>();
		outputParams.add("EV_IFRESULT");
		outputParams.add("EV_IFMSG");
		
		LOGGER.info("RFC [" + RFC_FUNC_NAME + "] Call!");
		
		String[] outTables = new String[3];
		outTables[0] = RFC_OUT_TABLE1;
		outTables[1] = RFC_OUT_TABLE2;
		outTables[2] = RFC_OUT_TABLE3;
		Map<String, Object> output = new RfcSearcher().callFunction(RFC_FUNC_NAME, inputParams, outputParams, outTables);
		return output;
	}
	
	/**
	 * create product data
	 * 
	 * @param results
	 * @throws Exception
	 */
	public int createProductData(List<Map<String, Object>> results) {
		if(results.isEmpty()) 
			return 0;
		
		List<List<Object>> parameters = new ArrayList<List<Object>>();
		int resultCnt = results.size();
		for(int i = 0 ; i < resultCnt ; i++) {
			Map<String, Object> record = (Map<String, Object>)results.get(i);
			this.showMap(record);
			List<Object> parameter = new ArrayList<Object>();
			parameter.add(record.get("IFSEQ"));
			parameter.add(record.get("WERKS"));
			parameter.add(record.get("MATNR"));
			parameter.add(record.get("MAKTX"));
			parameter.add(record.get("MTART"));
			parameter.add(record.get("MEINS"));
			parameter.add(record.get("MATKL"));
			parameter.add(record.get("BESKZ"));
			parameter.add(record.get("SOBSL"));
			parameter.add(record.get("MMSTA"));
			parameter.add(record.get("BSTMA"));
			parameter.add(record.get("MPQNT"));
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

		int updCnt = 0;
		try {
			updCnt = new MesUpdater().update(INSERT_PRODUCT_SQL, parameters);
		} catch (Throwable th) {
			LOGGER.severe(th.getMessage());
		}
		
		return updCnt;
	}
	
	/**
	 * create bom data
	 * 
	 * @param results
	 * @throws Exception
	 */
	public int createBomData(List<Map<String, Object>> results) throws Exception {
		if(results.isEmpty()) 
			return 0;
		
		List<List<Object>> parameters = new ArrayList<List<Object>>();
		int resultCnt = results.size();
		for(int i = 0 ; i < resultCnt ; i++) {
			Map<String, Object> record = (Map<String, Object>)results.get(i);
			this.showMap(record);
			
			if(this.isEmpty(record.get("IFSEQ")) || this.isEmpty(record.get("WERKS")) || this.isEmpty(record.get("STLAN")) || this.isEmpty(record.get("STLAL")) || this.isEmpty(record.get("IDNRK"))) {
				LOGGER.log(Level.WARNING, null, "Required field is empty!");
				continue;
			}

			List<Object> parameter = new ArrayList<Object>();
			parameter.add(record.get("IFSEQ"));
			parameter.add(record.get("WERKS"));
			parameter.add(record.get("MATNR"));
			parameter.add(record.get("STLAN"));
			parameter.add(record.get("STLAL"));
			parameter.add(record.get("IDNRK"));
			parameter.add(record.get("MENGE"));
			parameter.add(record.get("MEINS"));
			parameter.add(record.get("DATUV"));
			parameter.add(record.get("DATUB"));
			parameter.add(record.get("ERDAT"));
			parameter.add(record.get("ERZET"));
			parameter.add(record.get("ERNAM"));
			parameter.add(record.get("AEDAT"));
			parameter.add(record.get("AEZET"));
			parameter.add(record.get("AENAM"));
			parameter.add(this.isEmpty(record.get("IFRESULT")) ? "S" : record.get("IFRESULT"));
			parameter.add(record.get("IFMSG"));
			parameter.add("N");
			parameters.add(parameter);
		}

		int updCnt = 0;
		try {
			updCnt = new MesUpdater().update(INSERT_BOM_SQL, parameters);
		} catch (Throwable th) {
			LOGGER.severe(th.getMessage());
		}
		
		return updCnt;
	}
	
	/**
	 * create product data
	 * 
	 * @param results
	 * @throws Exception
	 */
	public int createBatchData(List<Map<String, Object>> results) throws Exception {
		if(results.isEmpty()) 
			return 0;
		
		List<List<Object>> parameters = new ArrayList<List<Object>>();
		int resultCnt = results.size();
		for(int i = 0 ; i < resultCnt ; i++) {
			Map<String, Object> record = (Map<String, Object>)results.get(i);
			this.showMap(record);
			List<Object> parameter = new ArrayList<Object>();
			// IFSEQ,WERKS,MATNR,CHARG,NAME1,LVORM,ERDAT,ERZET,ERNAM,AEDAT,AEZET,AENAM,IFRESULT,IFFMSG,MES_STAT,MES_ISTDT
			parameter.add(record.get("IFSEQ"));
			parameter.add(record.get("WERKS"));
			parameter.add(record.get("MATNR"));
			parameter.add(record.get("CHARG"));
			parameter.add(record.get("NAME1"));
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

		int updCnt = 0;
		try {
			updCnt = new MesUpdater().update(INSERT_BATCH_SQL, parameters);
		} catch (Throwable th) {
			LOGGER.severe(th.getMessage());
		}
		
		return updCnt;	
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
				List<Map<String, Object>> bomResults = (List<Map<String, Object>>)output.get(RFC_OUT_TABLE2);
				List<Map<String, Object>> batchResults = (List<Map<String, Object>>)output.get(RFC_OUT_TABLE3);
				resultCount = this.createProductData(matResults);
				info("Got (" + resultCount + ") Product From SAP!");
				resultCount = this.createBomData(bomResults);
				info("Got (" + resultCount + ") BOM From SAP!");
				resultCount = this.createBatchData(batchResults);
				info("Got (" + resultCount + ") Batch From SAP!");
			} else {
				info("Failed to get BOM From SAP!");
			}			
		} catch (Throwable th) {
			LOGGER.info("Failed to get BOM From SAP!" + th.getMessage());
		}	
	}
	
	private void info(String msg) {
		LOGGER.info(msg);
	}
	
	private boolean isEmpty(Object obj) {
		return (obj == null || obj.toString().equals("")) ? true : false;
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
