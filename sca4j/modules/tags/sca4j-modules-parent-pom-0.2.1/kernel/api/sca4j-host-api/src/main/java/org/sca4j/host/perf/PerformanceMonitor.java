package org.sca4j.host.perf;

import java.util.Stack;

/**
 * Monitor performance.
 * 
 * TODO This is just a stop gap solution, I am working on something more permanent.
 *
 */
public class PerformanceMonitor {

	private static Stack<String> events = new Stack<String>();
	private static Stack<Long> now = new Stack<Long>();
	private static final boolean MONITOR = System.getProperty("sca4j.monitor") != null;
	
	public static void start(String event) {
		if (!MONITOR) {
			return;
		}
		events.push(event);
		now.push(System.currentTimeMillis());
	}
	
	public static void end() {
		if (!MONITOR) {
			return;
		}
		long duration = System.currentTimeMillis() - now.pop();
		String event = events.pop();
		
		if (duration > 500) {
			System.err.println(event + ":" + duration);
		}
	}
}
