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
