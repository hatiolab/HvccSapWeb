package com.hvcc.sap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Servlet Context Listener
 * 
 * @author Shortstop
 */
public class SapContextListener implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(SapContextListener.class.getName());
	private ExecutorService executor;
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
        this.executor.shutdownNow();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		LOGGER.info("Sap Context Listener started!");
		//ServletContext context = event.getServletContext();
        ThreadFactory daemonFactory = new SapThreadFactory();
        this.executor = Executors.newSingleThreadExecutor(daemonFactory);
        SapThread task = new SapThread();
        this.executor.submit(task);
	}

}
