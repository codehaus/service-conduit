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
package org.sca4j.idea;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

/**
 * Instantiates a test configuration model for JUnit components.
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
class F3ConfigurationFactory extends ConfigurationFactory {

    public F3ConfigurationFactory(F3ConfigurationType type) {
        super(type);
    }

    public RunConfiguration createTemplateConfiguration(Project project) {
        return new F3JUnitRunConfiguration(project, this, "");
    }

    public RunConfiguration createConfiguration(String name, RunConfiguration template) {
        F3JUnitRunConfiguration config = (F3JUnitRunConfiguration) template;
        if (config.getModule() == null) {
            final Module[] modules = config.getModules();
            if (modules != null && modules.length > 0) {
                config.setModule(modules[0]);
            }
        }
        return super.createConfiguration(name, config);
    }
}
