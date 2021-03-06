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
package org.sca4j.pojo.component;

import java.util.List;

import javax.security.auth.Subject;

import org.oasisopen.sca.ServiceReference;
import org.sca4j.api.SCA4JRequestContext;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 5455 $ $Date: 2008-09-21 06:26:43 +0100 (Sun, 21 Sep 2008) $
 */
public class PojoRequestContext implements SCA4JRequestContext {
    public Subject getSecuritySubject() {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        return workContext.getSubject();
    }

    public String getServiceName() {
        return null;
    }

    public <B> ServiceReference<B> getServiceReference() {
        return null;
    }

    public <CB> CB getCallback() {
        return null;
    }

    public <CB> ServiceReference<CB> getCallbackReference() {
        return null;
    }

    public <T> T getHeader(Class<T> type, String name) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        return workContext.getHeader(type, name);
    }

    public void setHeader(String name, Object value) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        workContext.setHeader(name, value);
    }

    public void removeHeader(String name) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        workContext.removeHeader(name);
    }

    @Override
    public void addHeader(String name, Object value) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        workContext.addHeader(name, value);
        
    }

    @Override
    public <T> List<T> getHeaders(Class<T> type, String name) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        return workContext.getHeaders(type, name);
    }
}
