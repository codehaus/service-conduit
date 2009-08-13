/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.binding.ws.axis2.runtime.config;

import org.apache.axis2.util.threadpool.ThreadFactory;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;

/**
 * Wrapper to use the SCA4J work scheduler to handle work from the Axis2 extension.
 *
 * @version $Revision$ $Date$
 */
public class SCA4JThreadFactory implements ThreadFactory {
    private WorkScheduler scheduler;

    public SCA4JThreadFactory(WorkScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void execute(final Runnable runnable) {
        scheduler.scheduleWork(new DefaultPausableWork() {
        	public void execute() {
        		runnable.run();
        	}
        });
    }
}
