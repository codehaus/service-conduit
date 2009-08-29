/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.timer.quartz;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.JobRunShellFactory;
import org.quartz.core.SchedulingContext;

/**
 * JobRunShell that wraps job invocations in a transaction.
 *
 * @version $Revision$ $Date$
 */
public class TrxJobRunShell extends SCA4JJobRunShell {
    private TransactionManager tm;

    public TrxJobRunShell(JobRunShellFactory shellFactory, Scheduler scheduler, TransactionManager tm, SchedulingContext context) {
        super(shellFactory, scheduler, context);
        this.tm = tm;
    }

    protected void begin() throws SchedulerException {
        beginTransaction();
        super.begin();
    }

    protected void complete(boolean successfull) throws SchedulerException {
        super.complete(successfull);
        if (successfull) {
            commitTransaction();
        } else {
            rollbackTransaction();
        }
    }

    private void beginTransaction() throws SchedulerException {
        try {
            tm.begin();
        } catch (NotSupportedException e) {
            throw new SchedulerException(e);
        } catch (SystemException e) {
            throw new SchedulerException(e);
        }
    }

    private void commitTransaction() throws SchedulerException {
        try {
            if (tm.getStatus() != Status.STATUS_MARKED_ROLLBACK) {
                tm.commit();
            } else {
                tm.rollback();
            }
        } catch (SystemException e) {
            throw new SchedulerException(e);
        } catch (IllegalStateException e) {
            throw new SchedulerException(e);
        } catch (SecurityException e) {
            throw new SchedulerException(e);
        } catch (HeuristicMixedException e) {
            throw new SchedulerException(e);
        } catch (HeuristicRollbackException e) {
            throw new SchedulerException(e);
        } catch (RollbackException e) {
            throw new SchedulerException(e);
        }
    }

    private void rollbackTransaction() throws SchedulerException {
        try {
            tm.rollback();
        } catch (SystemException e) {
            throw new SchedulerException(e);
        }
    }

}
