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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.host.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public abstract class AbstractHostInfo implements HostInfo {
    
    private static final String HOME_DIR = System.getProperty("user.home") + "/.sca4j";
    private static final String CONFIG_DIR = System.getenv("SCA4J_CONFIG_DIR");
    
    private final URI domain;
    private final Properties properties = new Properties();
    
    public AbstractHostInfo(URI domain, Properties properties) {
        this.domain = domain;
        this.properties.putAll(properties);
        loadProperties(domain);
    }

    public URI getDomain() {
        return domain;
    }

    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    private void loadProperties(URI domain) {
        if (CONFIG_DIR != null) {
            load(CONFIG_DIR);
        } else {
            load(HOME_DIR);
        }
    }

    private void load(String dir) {
        File configDir = new File(dir);
        if (configDir.exists()) {
            File configFile = new File(configDir, "global.properties");
            loadFile(configFile);
            configFile = new File(configDir, domain + ".properties");
            loadFile(configFile);
        }
    }

    private void loadFile(File globalConfigFile) {
        
        try {
            if (globalConfigFile.exists()) {
                FileInputStream stream = new FileInputStream(globalConfigFile);
                Properties temp = new Properties();
                temp.load(stream);
                this.properties.putAll(temp);
                stream.close();
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        
    }

}
