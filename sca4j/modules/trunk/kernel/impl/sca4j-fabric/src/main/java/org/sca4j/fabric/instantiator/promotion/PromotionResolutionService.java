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
package org.sca4j.fabric.instantiator.promotion;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * Resolves promoted services and references by setting the resolved promotion URI of the logical component service or reference that is being
 * promoted.
 *
 * @version $Revision$ $Date$
 */
public interface PromotionResolutionService {

    /**
     * Handles promotion on the specified logical service.
     * <p/>
     * Promoted URIs are of the general form <code>componentId#serviceName</code>, where the service name is optional. If the  promoted URI doesn't
     * contain a fragment for the service name, the promoted component is expected to have exactly one service. If the service fragment is present the
     * promoted component is required to have a service by the name. If the service fragment was not specified, the promoted URI is set to the URI of
     * the promoted service.
     *
     * @param logicalService Logical service whose promotion is handled.
     * @param change         the logical change associated with the deployment operation resolution is being performed for. Recoverable errors and
     *                       warnings should be reported here.
     */
    void resolve(LogicalService logicalService, LogicalChange change);

    /**
     * Handles all promotions on the specified logical reference.
     * <p/>
     * Promoted URIs are of the general form <code>componentId#referenceName</code>, where the reference name is optional. If the  promoted URI
     * doesn't contain a fragment for the reference name, the promoted component is expected to have exactly one reference. If the reference fragment
     * is present the promoted component is required to have a reference by the name. If the reference fragment was not specified, the promoted URI is
     * set to the URI of the promoted reference.
     *
     * @param logicalReference Logical reference whose promotion is handled.
     * @param change           the logical change associated with the deployment operation resolution is being performed for. Recoverable errors and
     *                         warnings should be reported here.
     */
    void resolve(LogicalReference logicalReference, LogicalChange change);

}
