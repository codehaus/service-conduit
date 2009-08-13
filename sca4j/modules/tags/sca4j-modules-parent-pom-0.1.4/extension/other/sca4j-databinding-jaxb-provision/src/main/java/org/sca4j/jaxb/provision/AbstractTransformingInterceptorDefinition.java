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
