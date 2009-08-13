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

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.scdl.Implementation;

/**
 * Implementation type for mock components.
 * 
 * @version $Revision$ $Date$
 */
public class ImplementationMock extends Implementation<MockComponentType> {
    private static final long serialVersionUID = -3519206465795353416L;

    static final QName IMPLEMENTATION_MOCK = new QName(Namespaces.SCA4J_NS, "implementation.mock");
    
    private final List<String> mockedInterfaces;

    /**
     * Initializes the mocked interfaces.
     * 
     * @param mockedInterfaces Mocked interfaces.
     */
    public ImplementationMock(List<String> mockedInterfaces, MockComponentType componentType) {
        super(componentType);
        this.mockedInterfaces = mockedInterfaces;
    }
    
    /**
     * Gets the interfaces that are mocked.
     * 
     * @return Interfaces that are mocked.
     */
    public List<String> getMockedInterfaces() {
        return mockedInterfaces;
    }
    
    /**
     * Gets the component type qualified name.
     */
    @Override
    public QName getType() {
        return IMPLEMENTATION_MOCK;
    }

}
