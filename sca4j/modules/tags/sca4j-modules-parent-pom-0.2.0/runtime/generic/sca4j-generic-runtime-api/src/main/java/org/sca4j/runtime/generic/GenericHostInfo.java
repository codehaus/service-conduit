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
package org.sca4j.runtime.generic;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.sca4j.host.runtime.AbstractHostInfo;
import org.sca4j.host.runtime.HostInfo;

public class GenericHostInfo extends AbstractHostInfo implements HostInfo {
	
	private boolean live;

    /**
     * Initialises the state.
     * 
     * @param domain Name of the domain.
     * @param baseDir Base directory.
     * @param properties Host properties.
     */
    public GenericHostInfo(URI domain, Properties properties, boolean live) {
        super(domain, properties);
        this.live = live;
    }

    /**
     * @see org.sca4j.host.runtime.HostInfo#getBaseDir()
     */
    public File getBaseDir() {
        return null;
    }

    /**
     * @see org.sca4j.host.runtime.HostInfo#getTempDir()
     */
    public File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    /**
     * @see org.sca4j.host.runtime.HostInfo#supportsClassLoaderIsolation()
     */
    public boolean supportsClassLoaderIsolation() {
        return false;
    }
    
    /**
     * @see org.sca4j.host.runtime.HostInfo#isLive()
     */
    public boolean isLive() {
    	return live;
    }

}
