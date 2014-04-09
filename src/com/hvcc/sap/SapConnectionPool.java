/**
 * 
 */
package com.hvcc.sap;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

/**
 * SAP Connection Pool
 * 
 * @author Shortstop
 */
public class SapConnectionPool {
	
	private String SID = null;
	private static SapConnectionPool instance = null;
	
	public SapConnectionPool(String sid, int maxcons, String client, String userid, String passwd, String lang, String server, String sysno) throws Exception {
		try {
			// Create SAP Connection Pool
			JCO.addClientPool(	sid, 
								maxcons, 
								client,
								userid,
								passwd,
								lang,
								server, 
								sysno);
			SID = sid;
		} catch (Exception _ex) {
			throw _ex;
		}
	}

	public static SapConnectionPool getInstance() throws Exception {
		if (instance == null) { 
		      try {
				String sID = "HCP";
				int sMaxCon = 10;
				String sClient = "100";
				String sUser = "MES_USER";
				String sPassword = "1qaz2wsx";
				String sLanguage = null;
				String sHostName = "190.1.5.170";
				String sSystem = "00";
				instance = new SapConnectionPool(sID, sMaxCon, sClient, sUser, sPassword, sLanguage, sHostName, sSystem);
		      } catch(Exception e) {
		    	  throw e;
		      }
		}

		return instance;
	}
	
	public JCO.Function createFunction(IRepository mRepository, String name) throws Exception {
		return mRepository.getFunctionTemplate(name.toUpperCase()).getFunction();
	}

	public JCO.Client getConnection() throws Exception {
		return JCO.getClient(SID);
	} 
	
	public void releaseConnection(JCO.Client connection) {
		try {
			JCO.releaseClient(connection);
		} catch (Exception _ex) {
			System.out.println("SAPConnectionPool:releaseConnection Error:" + _ex.toString());
		}
	}
}
