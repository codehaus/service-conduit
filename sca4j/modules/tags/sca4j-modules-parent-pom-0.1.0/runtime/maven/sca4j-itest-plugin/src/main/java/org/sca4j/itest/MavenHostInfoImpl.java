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

import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.io.File;

import org.sca4j.maven.runtime.MavenHostInfo;

/**
 * @version $Rev: 4941 $ $Date: 2008-07-01 17:55:55 +0100 (Tue, 01 Jul 2008) $
 */
public class MavenHostInfoImpl implements MavenHostInfo {
    private final URI domain;
    private final Properties hostProperties;
    private final Set<URL> dependencyUrls;
    private final File tempDir;

    public MavenHostInfoImpl(URI domain, Properties hostProperties, Set<URL> dependencyUrls) {
        this.domain = domain;
        this.hostProperties = hostProperties;
        this.dependencyUrls = dependencyUrls;
        this.tempDir = new File(System.getProperty("java.io.tmpdir"), ".sca4j");;
    }

    public File getBaseDir() {
        return null;
    }

    public boolean isOnline() {
        throw new UnsupportedOperationException();
    }

    public String getProperty(String name, String defaultValue) {
        return hostProperties.getProperty(name, defaultValue);
    }

    public boolean supportsClassLoaderIsolation() {
        return true;
    }

    public URI getDomain() {
        return domain;
    }

    public Set<URL> getDependencyUrls() {
        return dependencyUrls;
    }

    public File getTempDir() {
        return tempDir;
    }
}
