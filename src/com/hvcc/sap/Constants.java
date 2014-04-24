package com.hvcc.sap;

import java.io.InputStream;
import java.util.Properties;

public class Constants {

	public static String SAP_SID;
	public static int SAP_MAX_CON;
	public static String SAP_CLIENT;
	public static String SAP_USER;
	public static String SAP_PASSWORD;
	public static String SAP_HOST;
	public static String SAP_SYSTEM;
	public static String SAP_LANG;
	
	public static String SAP_DATEFORMAT;
	public static long EXE_INTERVAL;
	public static long EXE_RFC_INTERVAL;
	
	static {
		InputStream is = Constants.class.getResourceAsStream("/resources/sap.config.properties");
	    Properties props = new Properties();
	    try {
	    	props.load(is);
	    	SAP_SID = props.getProperty("sap.sid", "GQA");
	    	SAP_MAX_CON = Integer.parseInt(props.getProperty("sap.max_con", "10"));
	    	SAP_CLIENT = props.getProperty("sap.client", "120");
	    	SAP_USER = props.getProperty("sap.user", "C000-I002");
	    	SAP_PASSWORD = props.getProperty("sap.password", "hvccglobal");
	    	SAP_HOST = props.getProperty("sap.ip", "sapqas.hvccglobal.com");
	    	SAP_SYSTEM = props.getProperty("sap.system", "00");
	    	SAP_LANG = props.getProperty("sap.language", "ZH");
	    	
	    	SAP_DATEFORMAT = props.getProperty("sap.dateformat", "yyyyMMdd");
	    	EXE_INTERVAL = Integer.parseInt(props.getProperty("exe.interval", "60000"));
	    	EXE_RFC_INTERVAL = Integer.parseInt(props.getProperty("exe.rfc.interval", "5000"));
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
}
