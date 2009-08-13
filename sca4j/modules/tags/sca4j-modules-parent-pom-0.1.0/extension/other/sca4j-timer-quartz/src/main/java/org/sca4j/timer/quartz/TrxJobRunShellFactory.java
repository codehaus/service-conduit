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

package org.sca4j.timer.quartz;

import javax.transaction.TransactionManager;

import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.core.JobRunShell;
import org.quartz.core.JobRunShellFactory;
import org.quartz.core.SchedulingContext;

/**
 * Factory for the standard JobRunShell that wraps job invocations in a transaction.
 *
 * @version $Revision$ $Date$
 */
public class TrxJobRunShellFactory implements JobRunShellFactory {
    private TransactionManager tm;
    private TrxJobRunShell shell;

    public TrxJobRunShellFactory(TransactionManager tm) {
        this.tm = tm;
    }

    public void initialize(Scheduler scheduler, SchedulingContext context) throws SchedulerConfigException {
        shell = new TrxJobRunShell(this, scheduler, tm, context);
    }

    public JobRunShell borrowJobRunShell() throws SchedulerException {
        return shell;
    }

    public void returnJobRunShell(JobRunShell jobRunShell) {
        // no-op
    }
}
