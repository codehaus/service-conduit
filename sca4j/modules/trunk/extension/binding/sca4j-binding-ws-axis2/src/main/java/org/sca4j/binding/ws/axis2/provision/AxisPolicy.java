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
package org.sca4j.binding.ws.axis2.provision;

import org.w3c.dom.Element;

/**
 * Axis policy definition.
 * 
 * @version $Revision$ $Date$
 */
public final class AxisPolicy {
    
    private final String message;
    private final String module;
    private final Element opaquePolicy;
    
    /**
     * Initializes the message, module and policy definition.
     * 
     * @param message Message against in which policy is applied.
     * @param module Axis module needs to engage for the policy.
     * @param opaquePolicy Opaque policy definition.
     */
    public AxisPolicy(final String message, final String module, final Element opaquePolicy) {
        this.message = "".equals(message) ? null : message;
        this.module = module;
        this.opaquePolicy = opaquePolicy;
    }

    /**
     * Gets the message against which the policy is applied.
     * 
     * @return Message against which the policy is applied.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Name of the Axis module to be enagged for the policy.
     * 
     * @return Axis module to be engaged for the policy.
     */
    public String getModule() {
        return module;
    }

    /**
     * Gets the opaque policy definition.
     * 
     * @return Opaque XML policy definition.
     */
    public Element getOpaquePolicy() {
        return opaquePolicy;
    }

}
