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

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.easymock.IMocksControl;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilder;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class MockComponentBuilder<T> implements ComponentBuilder<MockComponentDefinition, MockComponent<T>> {
    
    private final ComponentBuilderRegistry builderRegistry;
    private final ClassLoaderRegistry classLoaderRegistry;
    private final IMocksControl control;
    
    public MockComponentBuilder(@Reference ComponentBuilderRegistry builderRegistry,
                                @Reference ClassLoaderRegistry classLoaderRegistry,
                                @Reference IMocksControl control) {
        this.builderRegistry = builderRegistry;
        this.classLoaderRegistry = classLoaderRegistry;
        this.control = control;
    }
    
    @Init
    public void init() {
        builderRegistry.register(MockComponentDefinition.class, this);
    }

    public MockComponent<T> build(MockComponentDefinition componentDefinition) throws BuilderException {
        
        List<String> interfaces = componentDefinition.getInterfaces();
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(componentDefinition.getClassLoaderId());

        List<Class<?>> mockedInterfaces = new LinkedList<Class<?>>();
        for(String interfaze : interfaces) {
            try {
                mockedInterfaces.add(classLoader.loadClass(interfaze));
            } catch (ClassNotFoundException ex) {
                throw new AssertionError(ex);
            }
        }

        ObjectFactory<T> objectFactory = new MockObjectFactory<T>(mockedInterfaces, classLoader, control);
        
        return new MockComponent<T>(componentDefinition.getComponentId(), objectFactory);
        
    }

}
