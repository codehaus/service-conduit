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
import java.util.HashMap;

import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.testset.TestSetFailedException;

/**
 * @version $Rev: 5328 $ $Date: 2008-09-04 12:43:14 +0100 (Thu, 04 Sep 2008) $
 */
public class SCATestSuite implements SurefireTestSuite {
    private final Map<String, SCATestSet> testSets = new HashMap<String, SCATestSet>();
    private int testSetCount = 0;
    private int testCount = 0;

    public void add(SCATestSet testSet) {
        testSets.put(testSet.getName(), testSet);
        testSetCount += 1;
        testCount += testSet.getTestCount();
    }

    public int getNumTests() {
        return testCount;
    }

    public int getNumTestSets() {
        return testSetCount;
    }

    public void execute(ReporterManager reporterManager, ClassLoader classLoader)
        throws ReporterException, TestSetFailedException {
        for (SCATestSet testSet : testSets.values()) {
            execute(testSet, reporterManager, classLoader);
        }
    }

    public void execute(String name, ReporterManager reporterManager, ClassLoader classLoader)
        throws ReporterException, TestSetFailedException {
        SCATestSet testSet = testSets.get(name);
        if (testSet == null) {
            throw new TestSetFailedException("Suite does not contain TestSet: " + name);
        }
        execute(testSet, reporterManager, classLoader);
    }

    protected void execute(SCATestSet testSet, ReporterManager reporterManager, ClassLoader classLoader)
        throws ReporterException, TestSetFailedException {
        reporterManager.testSetStarting(new ReportEntry(this, testSet.getName(), "Starting"));
        testSet.execute(reporterManager, classLoader);
        reporterManager.testSetCompleted(new ReportEntry(this, testSet.getName(), "Completed"));
        reporterManager.reset();
    }

    public Map<?, ?> locateTestSets(ClassLoader classLoader) throws TestSetFailedException {
        throw new UnsupportedOperationException();
    }
}
