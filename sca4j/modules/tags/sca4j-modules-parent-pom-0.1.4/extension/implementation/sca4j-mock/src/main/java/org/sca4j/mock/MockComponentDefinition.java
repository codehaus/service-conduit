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
package org.sca4j.mock;

import java.util.List;
import java.net.URI;

import org.sca4j.spi.model.physical.PhysicalComponentDefinition;

/**
 * @version $Revision$ $Date$
 */
public class MockComponentDefinition extends PhysicalComponentDefinition {
    private URI classLoaderId;
    private List<String> interfaces;

    /**
     * Gets the classloader id.
     *
     * @return Classloader id.
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Set the classloader id.
     *
     * @param classLoaderId Classloader id.
     */
    public void setClassLoaderId(URI classLoaderId) {
        this.classLoaderId = classLoaderId;
    }

    /**
     * @return List of interfaces that are mocked.
     */
    public List<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces List of interfaces that are mocked.
     */
    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

}
