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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.itest;

import java.util.Map;

import org.apache.maven.surefire.report.PojoStackTraceWriter;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.report.StackTraceWriter;
import org.apache.maven.surefire.testset.SurefireTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 5328 $ $Date: 2008-09-04 12:43:14 +0100 (Thu, 04 Sep 2008) $
 */
public class SCATestSet implements SurefireTestSet {
    private final String name;
    private Wire wire;

    public SCATestSet(String name, Wire wire) {
        this.name = name;
        this.wire = wire;
    }

    public void execute(ReporterManager reporterManager, ClassLoader loader) throws TestSetFailedException {
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            String operationName = entry.getKey().getName();
            reporterManager.testStarting(new ReportEntry(this, operationName, name));
            try {
                WorkContext workContext = new WorkContext();
                CallFrame frame = new CallFrame();
                workContext.addCallFrame(frame);

                MessageImpl msg = new MessageImpl();
                msg.setWorkContext(workContext);
                Message response = entry.getValue().getHeadInterceptor().invoke(msg);
                if (response.isFault()) {
                    throw new TestSetFailedException(operationName, (Throwable) response.getBody());
                }

                reporterManager.testSucceeded(new ReportEntry(this, operationName, name));

            } catch (TestSetFailedException e) {
                StackTraceWriter stw = new PojoStackTraceWriter(name, operationName, e.getCause());
                reporterManager.testFailed(new ReportEntry(this, operationName, name, stw));
                throw e;
            }
        }
    }

    public int getTestCount() {
        return wire.getInvocationChains().size();
    }

    public String getName() {
        return name;
    }

    public Class<?> getTestClass() {
        throw new UnsupportedOperationException();
    }
}
