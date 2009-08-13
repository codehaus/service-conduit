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

import java.net.URI;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionListener;
import javax.xml.namespace.QName;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.runtime.SCA4JRuntime;

/**
 * The contract for artifacts loaded in the web application classloader to comminicate with the SCA4J runtime loaded in a child classloader. For
 * example, filters and listeners may use this interface to notify the runtime of the web container events.
 *
 * @version $Rev: 5085 $ $Date: 2008-07-23 17:39:52 +0100 (Wed, 23 Jul 2008) $
 */
public interface WebappRuntime extends ServletRequestListener, HttpSessionListener, SCA4JRuntime<WebappHostInfo> {

    /**
     * Activates a composite in the domain.
     *
     * @param qName       the composite qualified name
     * @param componentId the id of the component that should be bound to the webapp
     * @throws DeploymentException   if there was a problem initializing the composite
     * @throws ContributionException if an error is found in the contribution. If validation errors are encountered, a ValidationException will be
     *                               thrown.
     */
    void activate(QName qName, URI componentId) throws ContributionException, DeploymentException;

    /**
     * Returns the ServletRequestInjector for the runtime.
     *
     * @return the ServletRequestInjector for the runtime
     */
    ServletRequestInjector getRequestInjector();

}
