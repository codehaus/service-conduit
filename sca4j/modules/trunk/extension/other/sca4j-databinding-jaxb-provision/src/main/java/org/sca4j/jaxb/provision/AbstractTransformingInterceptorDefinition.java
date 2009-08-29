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
package org.sca4j.jaxb.provision;

import java.net.URI;
import java.util.Set;
import javax.xml.namespace.QName;

import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;

/**
 * Base definition for an interceptor that performs a data transformation to or from JAXB objects.
 *
 * @version $Revision$ $Date$
 */
public abstract class AbstractTransformingInterceptorDefinition extends PhysicalInterceptorDefinition {
    private URI classLoaderId;
    private QName dataType;
    private Set<String> classNames;

    /**
     * Cosntructor.
     *
     * @param classLoaderId classloader id for loading parameter and fault types.
     * @param dataType      the data type the transformer must convert to and from
     * @param classNames    set of parameter and fault types the transformer must be able to convert
     */
    public AbstractTransformingInterceptorDefinition(URI classLoaderId, QName dataType, Set<String> classNames) {
        this.classLoaderId = classLoaderId;
        this.dataType = dataType;
        this.classNames = classNames;
    }

    /**
     * The classloader id for loading parameter and fault types.
     *
     * @return the classlaoder id
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * The set of parameter and fault types the transformer must be able to convert.
     *
     * @return the parameter and fault types
     */
    public Set<String> getClassNames() {
        return classNames;
    }

    /**
     * Returns the data type the transformer must convert to and from.
     *
     * @return the data type
     */
    public QName getDataType() {
        return dataType;
    }
}
