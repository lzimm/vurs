package net.vurs.util;

import org.slf4j.Logger;

public class SimpleProfiler {
	
	private Logger logger = null;
	private String name = null;
	
	private long startTime = 0;
	private long lastTime = 0;
	
	public SimpleProfiler(Logger logger, String name) {
		this.logger = logger;
		this.name = name;
		this.startTime = System.currentTimeMillis();
		this.lastTime = this.startTime;
	}
	
	public void profile(String label) {
		long curTime = System.currentTimeMillis();
		this.logger.info("Profiler - " + this.name + 
				" stage: " + label + 
				" time: " + (curTime - this.lastTime) + 
				" total: " + (curTime - this.startTime));
		this.lastTime = curTime;
	}
	
}
