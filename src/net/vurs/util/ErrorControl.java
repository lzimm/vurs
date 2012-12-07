package net.vurs.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorControl {
	private static Logger logger = LoggerFactory.getLogger(ErrorControl.class);
	
	public static void fatal(String message) {
		String caller = Thread.currentThread()
								.getStackTrace()[2].getClassName();
		
		StringBuilder builder = new StringBuilder("Caught fatal error in: ")
									.append(caller)
									.append(": ")
									.append(message);
		
		logger.error(builder.toString());
		System.exit(0);
	}

	public static void logException(Exception e) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		e.printStackTrace(ps);
		ps.close();
		logger.error(os.toString());
	}
	
}
