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
package org.sca4j.maven.runtime;

import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;

import org.apache.maven.surefire.testset.TestSetFailedException;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.runtime.SCA4JRuntime;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.Operation;

/**
 * Contract for the Maven runtime.
 *
 * @version $Rev: 5085 $ $Date: 2008-07-23 17:39:52 +0100 (Wed, 23 Jul 2008) $
 */
public interface MavenEmbeddedRuntime extends SCA4JRuntime<MavenHostInfo> {

    /**
     * Activates a composite by qualified name contained in the Maven module the runtime is currently executing for.
     *
     * @param base      the module output directory location
     * @param composite the composite qname to activate
     * @return the activated composite's component type
     * @throws ContributionException if a contribution is thrown. The cause may a ValidationException resulting from  errors in the contribution. In
     *                               this case the errors should be reported back to the user.
     * @throws DeploymentException   if there is an error activating the test composite
     */
    Composite activate(URL base, QName composite) throws ContributionException, DeploymentException;

    /**
     * Activates a composite by qualified name contained in the contribution source.
     *
     * @param source    the source of the contribution
     * @param composite the composite qname to activate
     * @return the activated composite's component type
     * @throws ContributionException if a contribution is thrown. The cause may a ValidationException resulting from  errors in the contribution. In
     *                               this case the errors should be reported back to the user.
     * @throws DeploymentException   if there is an error activating the test composite
     */
    Composite activate(ContributionSource source, QName composite) throws ContributionException, DeploymentException;

    /**
     * Activates a composite pointed to by the SCDL location.
     * <p/>
     * Note this method preserves backward compatibility through specifying the composite by location. When possible, use {@link
     * #activate(java.net.URL, javax.xml.namespace.QName)} instead.
     *
     * @param base         the module output directory location
     * @param scdlLocation the composite file location
     * @return the activated composite's component type
     * @throws DeploymentException   if there is an error activating the test composite
     * @throws ContributionException if a contribution is thrown. The cause may a ValidationException resulting from  errors in the contribution. In
     *                               this case the errors should be reported back to the user.
     */
    Composite activate(URL base, URL scdlLocation) throws ContributionException, DeploymentException;

    /**
     * Starts the component context
     *
     * @param compositeId the context id
     * @throws ContextStartException if an error starting the context is encountered
     */
    void startContext(URI compositeId) throws ContextStartException;

    /**
     * @param contextId     the context id assocated with the test
     * @param componentName the test component name
     * @param operation     the operation to invoke on the test service contract
     * @throws TestSetFailedException if a test case fails
     */
    void executeTest(URI contextId, String componentName, Operation<?> operation) throws TestSetFailedException;
}
