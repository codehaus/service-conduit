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
package org.sca4j.container.web.spi;

import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

import org.osoa.sca.ComponentContext;

import org.sca4j.pojo.reflection.Injector;

/**
 * Responsible for activating a web application in an embedded servlet container.
 *
 * @version $Revision$ $Date$
 */
public interface WebApplicationActivator {
    public static final String SERVLET_CONTEXT_SITE = "sca4j.servletContext";
    public static final String SESSION_CONTEXT_SITE = "sca4j.sessionContext";
    public static final String CONTEXT_ATTRIBUTE = "org.osoa.sca.ComponentContext";

    /**
     * Returns the classloader to use for the web component corresponding the given id
     *
     * @param componentId the web component id
     * @return the classloader
     */
    ClassLoader getWebComponentClassLoader(URI componentId);

    /**
     * Perform the activation, which will result in making the web application available for incoming requests to the runtime.
     *
     * @param contextPath         the context path the web application will be available at. The context path is relative to the absolute address of
     *                            the embedded servlet container.
     * @param uri                 the URI of the contribution containing the web application assets
     * @param parentClassLoaderId the id for parent classloader to use for the web application
     * @param injectors           the map of artifact ids to injectors. An artifact id identifies an artifact type such as a servlet class name or
     *                            ServletContext.
     * @param context             the component context for the web component
     * @return the servlet context associated with the activated web application
     * @throws WebApplicationActivationException
     *          if an error occurs activating the web application
     */
    ServletContext activate(String contextPath, URI uri, URI parentClassLoaderId, Map<String, List<Injector<?>>> injectors, ComponentContext context)
            throws WebApplicationActivationException;

    /**
     * Removes an activated web application
     *
     * @param uri the URI the web application was activated with
     * @throws WebApplicationActivationException
     *          if an error occurs activating the web application
     */
    void deactivate(URI uri) throws WebApplicationActivationException;

}
