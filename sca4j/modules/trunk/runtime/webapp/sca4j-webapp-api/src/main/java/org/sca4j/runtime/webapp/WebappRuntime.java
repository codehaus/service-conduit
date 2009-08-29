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
