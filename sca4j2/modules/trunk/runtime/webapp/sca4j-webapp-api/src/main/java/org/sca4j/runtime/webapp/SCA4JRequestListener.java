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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import static org.sca4j.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

/**
 * Notifies the SCA4J runtime of session creation and expiration events.
 * 
 * @version $Rev: 3499 $ $Date: 2008-03-31 01:16:09 +0100 (Mon, 31 Mar 2008) $
 */
public class SCA4JRequestListener implements ServletRequestListener {
    private WebappRuntime runtime;

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        ServletContext context = servletRequestEvent.getServletContext();
        WebappRuntime runtime = getRuntime(context);
        if (runtime != null) {
            runtime.requestInitialized(servletRequestEvent);
        } else {
            context.log("requestInitialized", new ServletException("SCA4J runtime not configured"));
        }
    }

    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        ServletContext context = servletRequestEvent.getServletContext();
        WebappRuntime runtime = getRuntime(context);
        if (runtime != null) {
            runtime.requestDestroyed(servletRequestEvent);
        }
    }

    private WebappRuntime getRuntime(ServletContext context) {
        if (runtime == null) {
            runtime = (WebappRuntime) context.getAttribute(RUNTIME_ATTRIBUTE);
        }
        return runtime;
    }
}
