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
package org.sca4j.web.runtime;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

import org.sca4j.container.web.spi.WebRequestTunnel;
import org.sca4j.host.SCA4JRuntimeException;
import org.sca4j.spi.ObjectCreationException;

/**
 * Implementation of ComponentContext for Web components.
 *
 * @version $Rev: 1363 $ $Date: 2007-09-20 16:16:35 -0700 (Thu, 20 Sep 2007) $
 */
public class WebComponentContext implements ComponentContext {
    private final WebComponent<?> component;

    public WebComponentContext(WebComponent<?> component) {
        this.component = component;
    }

    public String getURI() {
        try {
            return component.getUri().toString();
        } catch (SCA4JRuntimeException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        }
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        try {
            return (R) component.cast(target);
        } catch (SCA4JRuntimeException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        }
    }

    public <B> B getService(Class<B> interfaze, String referenceName) {
        try {
            return interfaze.cast(getSession().getAttribute(referenceName));
        } catch (SCA4JRuntimeException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        }
    }

    // method is a proposed spec change
    public <B> B createService(Class<B> interfaze, String referenceName) {
        try {
            return component.getService(interfaze, referenceName);
        } catch (SCA4JRuntimeException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        } catch (ObjectCreationException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public <B> ServiceReference<B> getServiceReference(Class<B> interfaze, String referenceName) {
        try {
            return ServiceReference.class.cast(getSession().getAttribute(referenceName));
        } catch (SCA4JRuntimeException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        }
    }

    public <B> B getProperty(Class<B> type, String propertyName) {
        try {
            return component.getProperty(type, propertyName);
        } catch (ObjectCreationException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        } catch (SCA4JRuntimeException e) {
            throw new ServiceRuntimeException(e.getMessage(), e);
        }
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface) {
        return null;
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, String serviceName) {
        return null;
    }

    public RequestContext getRequestContext() {
        return null;
    }

    private HttpSession getSession() {
        HttpServletRequest request = WebRequestTunnel.getRequest();
        if (request == null) {
            throw new ServiceRuntimeException("HTTP request not bound. Check filter configuration.");
        }
        return request.getSession(true);  // force creation of session 
    }


}
