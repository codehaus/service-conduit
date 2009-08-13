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
package org.sca4j.idea.run;

import java.net.URI;
import java.util.Collection;

import org.apache.maven.surefire.report.PojoStackTraceWriter;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.report.StackTraceWriter;
import org.apache.maven.surefire.testset.SurefireTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.sca4j.maven.runtime.MavenEmbeddedRuntime;
import org.sca4j.scdl.Operation;

/**
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
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

    public Class getTestClass() {
        throw new UnsupportedOperationException();
    }
}
