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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

/**
 * @author meerajk
 *
 */
public class SCA4JComponentProviderFactory implements IoCComponentProviderFactory {

    ConcurrentHashMap<Class<?>, SCA4JComponentProvider> providers = new ConcurrentHashMap<Class<?>, SCA4JComponentProvider>();

    /**
     * {@inheritDoc}
     */
    public IoCComponentProvider getComponentProvider(Class<?> componentClass) {
        return providers.get(componentClass);
    }

    /**
     * {@inheritDoc}
     */
    public IoCComponentProvider getComponentProvider(ComponentContext componentContxt, Class<?> componentClass) {
        return providers.get(componentClass);
    }

    /**
     * @return
     */
    public Set<Class<?>> getClasses() {
        return providers.keySet();
    }

    public void addServiceHandler(Class<?> resource, Object instance) {
        providers.put(resource, new SCA4JComponentProvider(instance));
    }

}
