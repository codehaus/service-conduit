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
package org.sca4.runtime.generic.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.sca4j.fabric.runtime.AbstractRuntime;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.monitor.impl.JavaLoggingMonitorFactory;
import org.sca4j.runtime.generic.GenericHostInfo;
import org.sca4j.runtime.generic.GenericRuntime;

/**
 * Default implementation of the generic runtime.
 * 
 * @author meerajk
 *
 */
public class GenericRuntimeImpl extends AbstractRuntime<GenericHostInfo> implements GenericRuntime {
    
    private static GenericRuntimeImpl INSTANCE = null;
    
    /**
     * Gets the singleton instance of the generic runtime.
     * @return Singleton instance of the generic runtime.
     * @throws IOException If unable to scan the classpath.
     */
    public static synchronized  GenericRuntime getInstance(URI domain, Properties hostProperties) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new GenericRuntimeImpl(domain, hostProperties);
        }
        return INSTANCE;
    }
    
    /**
     * Contributes a deployable to the domain.
     * 
     * @param deployable Qualified name of the deployable.
     * @param extension Whether this is an extension or a user contribution.
     */
    public void contriute(QName deployable, boolean extension) {
        
    }

    /**
     * @throws InitializationException 
     * @see org.sca4j.runtime.generic.GenericRuntime#boot()
     */
    public void boot() throws InitializationException {
        
        BootConfiguration bootConfiguration = getBootConfiguration();
        
        setMonitorFactory(new JavaLoggingMonitorFactory());
        bootPrimordial(bootConfiguration);
        bootSystem();
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#getServiceProxy(java.lang.Class, javax.xml.namespace.QName)
     */
    public <T> T getServiceProxy(Class<T> serviceClass, QName serviceName) {
        return null;
    }
    
    /*
     * Returns the boot configuration.
     */
    private BootConfiguration getBootConfiguration() {
        
        BootConfiguration bootConfiguration = new BootConfiguration();
        
        ClassLoader classLoader = getClass().getClassLoader();
        bootConfiguration.setAppClassLoader(classLoader);
        bootConfiguration.setBootClassLoader(classLoader);
        bootConfiguration.setHostClassLoader(classLoader);
        
        bootConfiguration.setRuntime(this);
        
//        bootConfiguration.setBootLibraryExports(bootExports);
//        bootConfiguration.setExtensions(extensions);
//        bootConfiguration.setIntents(intents);
//        bootConfiguration.setSystemConfig(systemConfig);
//        bootConfiguration.setSystemConfigDocument(systemConfigDocument);
//        bootConfiguration.setSystemScdl(systemScdl);
        
        return bootConfiguration;
        
    }
    
    /*
     * Singleton constructor.
     */
    private GenericRuntimeImpl(URI domain, Properties hostProperties) throws IOException {
        super(GenericHostInfo.class);
        setHostInfo(new GenericHostInfo(domain, hostProperties));
    }

}
