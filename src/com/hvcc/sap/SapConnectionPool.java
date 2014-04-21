/**
 * 
 */
package com.hvcc.sap;

import java.io.InputStream;
import java.util.Properties;

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
	
	public SapConnectionPool(String sid, int maxcons, String client, String userid, String passwd, String lang, String server, String sysno) 
	throws Exception {
		try {
			// Create SAP Connection Pool
			JCO.addClientPool(sid, maxcons, client, userid, passwd, lang, server, sysno);
			SID = sid;
		} catch (Throwable th) {
			throw new Exception(th);
		}
	}

	public static SapConnectionPool getInstance() throws Exception {
		if (instance == null) { 
		      try {
		    	Properties props = SapConnectionPool.loadProperties();
		    	String sID = props.getProperty("sap.sid", "GQA");
		    	int sMaxCon = Integer.parseInt(props.getProperty("max_con", "10"));
		    	String sClient = props.getProperty("client", "120");
		    	String sUser = props.getProperty("user", "C000-I002");
		    	String sPassword = props.getProperty("password", "hvccglobal");
		    	String sHostName = props.getProperty("ip", "sapqas.hvccglobal.com");
		    	String sSystem = props.getProperty("system", "00");
		    	String sLanguage = null;
				instance = new SapConnectionPool(sID, sMaxCon, sClient, sUser, sPassword, sLanguage, sHostName, sSystem);
		      } catch(Throwable th) {
		    	  throw new Exception(th);
		      }
		}

		return instance;
	}
	
	public static Properties loadProperties() throws Exception {
		InputStream is = SapConnectionPool.class.getResourceAsStream("/resources/sap.config.properties");
	    Properties props = new Properties();
	    props.load(is);
	    return props;
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
		} catch (Throwable th) {
			System.out.println("SAPConnectionPool:releaseConnection Error : " + th.toString());
		}
	}
}
