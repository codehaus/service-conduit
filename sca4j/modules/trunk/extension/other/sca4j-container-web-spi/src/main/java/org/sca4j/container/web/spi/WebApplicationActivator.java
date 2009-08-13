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
