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
