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
package org.sca4j.rs.runtime.rs;

import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;

/**
 * @version $Rev: 5452 $ $Date: 2008-09-20 11:40:07 +0100 (Sat, 20 Sep 2008) $
 */
public class SCA4JComponentProvider implements IoCInstantiatedComponentProvider {

    private Object instance;
    
    public SCA4JComponentProvider(Object instance) {
        this.instance = instance;
    }
    
    public Object getInstance() {
        return instance;
    }

    public Object getInjectableInstance(Object object) {
        return instance;
    }
}
