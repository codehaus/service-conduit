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
package org.sca4j.runtime.webapp;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.ServletContext;

import static org.sca4j.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

/**
 * Notifies the WebappRuntime of session events.
 *
 * @version $Revision$ $Date$
 */
public class SCA4JSessionListener implements HttpSessionListener {
    private WebappRuntime runtime;

    public void sessionCreated(HttpSessionEvent event) {
        getRuntime(event.getSession().getServletContext()).sessionCreated(event);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        getRuntime(event.getSession().getServletContext()).sessionDestroyed(event);
    }

    private WebappRuntime getRuntime(ServletContext context) {
        if (runtime == null) {
            runtime = (WebappRuntime) context.getAttribute(RUNTIME_ATTRIBUTE);
        }
        return runtime;
    }

}
