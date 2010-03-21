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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.spi.domain;

import javax.xml.namespace.QName;

import org.sca4j.host.domain.DeploymentException;
import org.sca4j.scdl.Composite;

/**
 * Represents a domain.
 *
 * @version $Rev: 5271 $ $Date: 2008-08-25 22:46:23 +0100 (Mon, 25 Aug 2008) $
 */
public interface Domain {

    /**
     * Include a deployable composite in the domain.
     *
     * @param deployable the name of the deployable composite to include
     * @throws DeploymentException if an error is encountered during inclusion
     */
    void include(QName deployable) throws DeploymentException;

    /**
     * Include a Composite in the domain.
     *
     * @param composite the composite to include
     * @throws DeploymentException if an error is encountered during inclusion
     */
    void include(Composite composite) throws DeploymentException;

    /**
     * Remove a deployable Composite from the domain.
     *
     * @param deployable the name of the deployable composite to remove
     * @throws org.sca4j.host.domain.DeploymentException
     *          if an error is encountered during removal
     */
    void remove(QName deployable) throws DeploymentException;

    /**
     * Remove a Composite from the domain.
     *
     * @param composite the composite to remove
     * @throws DeploymentException if an error is encountered during removal
     */
    void remove(Composite composite) throws DeploymentException;

}
