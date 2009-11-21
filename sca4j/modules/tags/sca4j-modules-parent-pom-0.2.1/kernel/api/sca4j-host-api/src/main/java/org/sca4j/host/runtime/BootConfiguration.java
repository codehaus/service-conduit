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
package org.sca4j.host.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.FileContributionSource;
import org.xml.sax.InputSource;

/**
 * Encapsulates configuration needed to boostrap a runtime.
 *
 * @version $Revision$ $Date$
 */
public class BootConfiguration {
    
    private SCA4JRuntime<?> runtime;
    private ClassLoader bootClassLoader;
    private ClassLoader appClassLoader;
    private ClassLoader hostClassLoader;
    private List<String> bootExports;
    private ContributionSource intents;
    private List<ContributionSource> extensions;
    private URL systemScdl;
    private InputSource systemConfigDocument;
    private URL systemConfig;

    public SCA4JRuntime<?> getRuntime() {
        return runtime;
    }

    public void setRuntime(SCA4JRuntime<?> runtime) {
        this.runtime = runtime;
    }

    public URL getSystemScdl() {
        return systemScdl;
    }

    public void setSystemScdl(URL systemScdl) {
        this.systemScdl = systemScdl;
    }

    public InputSource getSystemConfigDocument() {
        return systemConfigDocument;
    }

    public void setSystemConfigDocument(InputSource systemConfigDocument) {
        this.systemConfigDocument = systemConfigDocument;
    }

    public URL getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(URL systemConfig) {
        this.systemConfig = systemConfig;
    }

    public ClassLoader getHostClassLoader() {
        return hostClassLoader;
    }

    public void setHostClassLoader(ClassLoader hostClassLoader) {
        this.hostClassLoader = hostClassLoader;
    }

    public ClassLoader getBootClassLoader() {
        return bootClassLoader;
    }

    public void setBootClassLoader(ClassLoader bootClassLoader) {
        this.bootClassLoader = bootClassLoader;
    }

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public void setAppClassLoader(ClassLoader appClassLoader) {
        this.appClassLoader = appClassLoader;
    }

    public List<String> getBootLibraryExports() {
        return bootExports;
    }

    public void setBootLibraryExports(List<String> bootExports) {
        this.bootExports = bootExports;
    }

    public ContributionSource getIntents() {
        return intents;
    }

    public void setIntents(ContributionSource intents) {
        this.intents = intents;
    }

    public List<ContributionSource> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<ContributionSource> extensions) {
        this.extensions = extensions;
    }

}
