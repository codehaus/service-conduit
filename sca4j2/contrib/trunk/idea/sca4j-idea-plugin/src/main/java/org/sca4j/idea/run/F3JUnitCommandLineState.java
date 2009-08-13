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
import javax.xml.namespace.QName;

import com.intellij.execution.CantRunException;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;

/**
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public class F3JUnitCommandLineState extends JavaCommandLineState {
    private URI domainUri;
    private Module module;
    private String junitClass;
    private QName composite;

    public F3JUnitCommandLineState(Module module,
                                   String junitClass,
                                   QName composite,
                                   RunnerSettings runnerSettings,
                                   ConfigurationPerRunnerSettings perRunnerSettings) {
        super(runnerSettings, perRunnerSettings);
        this.junitClass = junitClass;
        this.composite = composite;
        domainUri = URI.create("sca4j://./domain");
        this.module = module;
    }

    public ExecutionResult execute() throws ExecutionException {
        ConsoleView view = TextConsoleBuilderFactory.getInstance().createBuilder(module.getProject()).getConsole();
        ProcessHandler handler = new F3ProcessHandler(domainUri, module, junitClass, composite, view);
        return new DefaultExecutionResult(view, handler);
    }

    protected JavaParameters createJavaParameters() throws ExecutionException {
        ModuleRootManager manager = ModuleRootManager.getInstance(module);
        ProjectJdk jdk = manager.getJdk();
        if (jdk == null) {
            throw CantRunException.noJdkForModule(module);
        }
        JavaParameters params = new JavaParameters();
        params.setJdk(jdk);
        return params;
    }
}
