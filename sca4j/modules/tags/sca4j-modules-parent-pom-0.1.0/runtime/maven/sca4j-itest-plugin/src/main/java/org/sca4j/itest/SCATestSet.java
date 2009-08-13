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

import java.util.Collection;
import java.net.URI;

import org.apache.maven.surefire.testset.SurefireTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.StackTraceWriter;
import org.apache.maven.surefire.report.PojoStackTraceWriter;

import org.sca4j.scdl.Operation;
import org.sca4j.maven.runtime.MavenEmbeddedRuntime;

/**
 * @version $Rev: 5328 $ $Date: 2008-09-04 12:43:14 +0100 (Thu, 04 Sep 2008) $
 */
public class SCATestSet implements SurefireTestSet {
    private final MavenEmbeddedRuntime runtime;
    private final String name;
    private final URI contextId;
    private final Collection<? extends Operation<?>> operations;

    public SCATestSet(MavenEmbeddedRuntime runtime,
                      String name,
                      URI contextId,
                      Collection<? extends Operation<?>> operations) {
        this.runtime = runtime;
        this.name = name;
        this.contextId = contextId;
        this.operations = operations;
    }

    public void execute(ReporterManager reporterManager, ClassLoader classLoader) throws TestSetFailedException {
        for (Operation<?> operation : operations) {
            String operationName = operation.getName();
            reporterManager.testStarting(new ReportEntry(this, operationName, name));
            try {
                runtime.executeTest(contextId, name, operation);
                reporterManager.testSucceeded(new ReportEntry(this, operationName, name));
            } catch (TestSetFailedException e) {
                StackTraceWriter stw = new PojoStackTraceWriter(name, operationName, e.getCause());
                reporterManager.testFailed(new ReportEntry(this, operationName, name, stw));
                throw e;
            }
        }
    }

    public int getTestCount() {
        return operations.size();
    }

    public String getName() {
        return name;
    }

    public Class<?> getTestClass() {
        throw new UnsupportedOperationException();
    }
}
