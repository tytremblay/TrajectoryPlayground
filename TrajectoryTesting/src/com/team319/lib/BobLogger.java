package com.team319.lib;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BobLogger {

	private Logger logger;

	public BobLogger(String name, String logLocation) {
		// Instantiate the logger with the given name
		this.logger = Logger.getLogger(name);
		FileHandler fh;

		try {
			// This block configures the logger with handler and formatter
			fh = new FileHandler(logLocation);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// try logging to make sure it works.
			logger.info("Logger initialized.");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * Sets the logging level of the logger.  If set to WARN, FINE and DEBUG logs
	 * are not written, helping performance.
	 *  
	 *More information on Logging Levels 
	 *https://docs.oracle.com/javase/7/docs/api/java/util/logging/Level.html
	 */
	public void setLevel(Level level)
	{		
		this.logger.setLevel(level);
	}

	/*
	 * Used to log with the following severity (desc order)
	 * ERROR - Something critical has happened.
	 * WARN - Potential problems
	 * INFO - Anything we want to know when looking at the log files.  (sensor states, robot states) 
	 * DEBUG - Only for debugging, should be disabled during a match.
	 * @author Ty Tremblay
	 *  
	 */
	public void error(String msg) {
		logger.severe(msg);
	}
	
	public void warn(String msg){
		logger.warning(msg);
	}
	
	public void info(String msg) {
		logger.info(msg);		
	}
	
	public void debug(String msg){
		logger.fine(msg);
	}
	
}
