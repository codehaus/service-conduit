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
package org.sca4j.runtime.webapp.activator;

import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.sca4j.container.web.spi.WebApplicationActivationException;
import org.sca4j.container.web.spi.WebApplicationActivator;
import org.sca4j.pojo.reflection.Injector;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.host.ServletHost;

/**
 * A WebApplicationActivator used in a runtime embedded in a WAR.
 *
 * @version $Revision$ $Date$
 */
@Service(interfaces = {WebApplicationActivator.class, HttpSessionListener.class})
public class EmbeddedWebApplicationActivator implements WebApplicationActivator, HttpSessionListener {
    private ServletHost host;
    private List<Injector<HttpSession>> sessionInjectors;

    public EmbeddedWebApplicationActivator(@Reference ServletHost host) {
        this.host = host;
    }

    public ClassLoader getWebComponentClassLoader(URI componentId) {
        //  As the runtime is embedded in a web app, the TCCL is the webapp classloader
        return Thread.currentThread().getContextClassLoader();
    }

    @SuppressWarnings({"unchecked"})
    public ServletContext activate(String contextPath,
                                   URI uri,
                                   URI parentClassLoaderId,
                                   Map<String, List<Injector<?>>> injectors,
                                   ComponentContext context) throws WebApplicationActivationException {
        try {
            // the web app has already been activated since it is embedded in a war. Just inject references and properties
            ServletContext servletContext = host.getServletContext();
            injectServletContext(servletContext, injectors);
            sessionInjectors = List.class.cast(injectors.get(SESSION_CONTEXT_SITE));
            return servletContext;
        } catch (ObjectCreationException e) {
            throw new WebApplicationActivationException(e);
        }
    }

    public void deactivate(URI uri) throws WebApplicationActivationException {
        // do nothing
    }

    @SuppressWarnings({"unchecked"})
    private void injectServletContext(ServletContext servletContext, Map<String, List<Injector<?>>> injectors) throws ObjectCreationException {
        List<Injector<?>> list = injectors.get(SERVLET_CONTEXT_SITE);
        if (list == null) {
            // nothing to inject
            return;
        }
        for (Injector injector : list) {
            injector.inject(servletContext);
        }
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        // inject conversational proxies into the session
        for (Injector<HttpSession> injector : sessionInjectors) {
            try {
                injector.inject(session);
            } catch (ObjectCreationException e) {
                throw new RuntimeInjectionException(e);
            }
        }
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        // do nothing
    }
}
