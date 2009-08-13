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
