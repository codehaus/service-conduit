package org.sca4j.spi.invocation;

public final class WorkContextTunnel {

	private static final ThreadLocal<WorkContext> CONTEXT = new ThreadLocal<WorkContext>();

	private WorkContextTunnel() {

	}

	public static WorkContext setThreadWorkContext(WorkContext context) {
		WorkContext old = CONTEXT.get();
		CONTEXT.set(context);
		return old;
	}

	public static WorkContext getThreadWorkContext() {
		return CONTEXT.get();
	}
}
