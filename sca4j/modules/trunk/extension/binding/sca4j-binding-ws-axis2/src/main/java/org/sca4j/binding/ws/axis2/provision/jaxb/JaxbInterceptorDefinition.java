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
package org.sca4j.binding.ws.axis2.provision.jaxb;

import java.net.URI;
import java.util.Set;

import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;

/**
 * @version $Revision$ $Date$
 */
public class JaxbInterceptorDefinition extends PhysicalInterceptorDefinition {
    
    private final URI classLoaderId;
    private final Set<String> classNames;
    private final Set<String> faultNames;
    private final boolean service;
    
    public JaxbInterceptorDefinition(URI classLoaderId, Set<String> classNames, Set<String> faultNames, boolean service) {
        this.classLoaderId = classLoaderId;
        this.classNames = classNames;
        this.faultNames = faultNames;
        this.service = service;
    }

    public URI getClassLoaderId() {
        return classLoaderId;
    }

    public Set<String> getClassNames() {
        return classNames;
    }

    public Set<String> getFaultNames() {
        return faultNames;
    }

    public boolean isService() {
        return service;
    }

}
