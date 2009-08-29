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
package org.sca4j.scdl;

/**
 * Identifies an attribute of the component that can be injected into an instance.
 *
 * @version $Revision: 5236 $ $Date: 2008-08-20 19:46:18 +0100 (Wed, 20 Aug 2008) $
 */
public class InjectableAttribute extends ModelObject {
    private static final long serialVersionUID = -3313258224983902890L;
    public static final InjectableAttribute COMPONENT_CONTEXT = new InjectableAttribute(InjectableAttributeType.CONTEXT, "ComponentContext");
    public static final InjectableAttribute REQUEST_CONTEXT = new InjectableAttribute(InjectableAttributeType.CONTEXT, "RequestContext");
    public static final InjectableAttribute CONVERSATION_ID = new InjectableAttribute(InjectableAttributeType.CONTEXT, "ConversationId");

    private InjectableAttributeType valueType;

    private String name;

    /**
     * Constructor used for deserialization.
     */
    public InjectableAttribute() {
    }

    /**
     * Constructor specifying type of value and logical name.
     *
     * @param valueType the type of value
     * @param name      the logical name
     */
    public InjectableAttribute(InjectableAttributeType valueType, String name) {
        this.valueType = valueType;
        this.name = name;
    }

    /**
     * Returns the type (service, reference, property).
     *
     * @return the type of value this source represents
     */
    public InjectableAttributeType getValueType() {
        return valueType;
    }

    /**
     * Sets the type (callback, reference, property).
     *
     * @param valueType the type of value this source represents
     */
    public void setValueType(InjectableAttributeType valueType) {
        this.valueType = valueType;
    }

    /**
     * Returns the name.
     *
     * @return the name of this value
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this value.
     *
     * @param name the name of this value
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name + '[' + valueType + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InjectableAttribute that = (InjectableAttribute) o;
        return name.equals(that.name) && valueType == that.valueType;

    }

    @Override
    public int hashCode() {
        return valueType.hashCode() * 31 + name.hashCode();
    }
}
