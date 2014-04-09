package com.hvcc.sap.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Servlet Context Listener 
 * 
 * @author Shortstop
 */
public class HvccSapServletContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
       System.out.println("Shutting down scheduler ....");
       Scheduler scheduer = null;
       
       try {
           scheduer = StdSchedulerFactory.getDefaultScheduler();
       } catch (SchedulerException e) {
           e.printStackTrace();
       }
       try {
           scheduer.shutdown();
			Thread.sleep(1000);
       } catch (SchedulerException se) {
           se.printStackTrace();
       } catch (InterruptedException ie) {
       }
   }

   public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("Starting up scheduler ...");
   }

}
