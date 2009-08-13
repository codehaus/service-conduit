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

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/**
 * Maps incoming requests to a web application context to a servlet provided by a binding.
 *
 * @version $Revision$ $Date$
 */
public class SCA4JServlet extends HttpServlet {
    private static final long serialVersionUID = 1548054328338375218L;
    private ServletRequestInjector requestInjector;

    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        WebappRuntime runtime = (WebappRuntime) servletContext.getAttribute(Constants.RUNTIME_ATTRIBUTE);
        if (runtime == null) {
            throw new ServletException("SCA4J runtime not configured for web application");
        }
        requestInjector = runtime.getRequestInjector();
        requestInjector.init(config);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        requestInjector.service(req, res);
    }
}
