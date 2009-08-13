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

import javax.swing.*;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public class F3ConfigurationType implements ConfigurationType {
    private ConfigurationFactory factory;
    private Logger logger;

    public F3ConfigurationType() {
        factory = new F3ConfigurationFactory(this);
        logger = Logger.getInstance("sca4j");
    }

    public void initComponent() {
        logger.info("SCA4J plugin initialized");
    }

    public void disposeComponent() {
        logger.info("SCA4J plugin shutdown");
    }

    @NotNull
    public String getComponentName() {
        return "F3ConfigurationType";
    }

    public String getDisplayName() {
        return "SCA4J JUnit Component";
    }

    public String getConfigurationTypeDescription() {
        return "SCA4J JUnit Component";
    }

    public Icon getIcon() {
        return null;
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{factory};
    }

}
