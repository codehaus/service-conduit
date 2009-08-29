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
