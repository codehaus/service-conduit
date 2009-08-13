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
package org.sca4j.fabric.implementation.singleton;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Implementation;

/**
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class SingletonImplementation extends Implementation<PojoComponentType> {
    private static final long serialVersionUID = -3874858273451538661L;
    public static final QName IMPLEMENTATION_SINGLETON = new QName(Namespaces.SCA4J_NS, "singleton");
    
    private String implementationClass;

    public SingletonImplementation() {
    }

    public SingletonImplementation(PojoComponentType componentType, String implementationClass) {
        super(componentType);
        this.implementationClass = implementationClass;
    }

    public QName getType() {
        return IMPLEMENTATION_SINGLETON;
    }

    public String getImplementationClass() {
        return implementationClass;
    }

}
