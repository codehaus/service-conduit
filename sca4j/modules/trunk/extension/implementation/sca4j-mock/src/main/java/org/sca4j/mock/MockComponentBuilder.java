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
