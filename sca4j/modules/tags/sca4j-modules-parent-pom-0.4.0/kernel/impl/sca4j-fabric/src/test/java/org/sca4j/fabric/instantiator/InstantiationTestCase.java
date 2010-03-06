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
package org.sca4j.fabric.instantiator;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

import org.sca4j.fabric.instantiator.component.AtomicComponentInstantiator;
import org.sca4j.fabric.instantiator.component.CompositeComponentInstantiator;
import org.sca4j.fabric.instantiator.component.WireInstantiator;
import org.sca4j.fabric.instantiator.component.WireInstantiatorImpl;
import org.sca4j.fabric.instantiator.normalize.PromotionNormalizer;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ComponentType;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.scdl.CompositeReference;
import org.sca4j.scdl.CompositeService;
import org.sca4j.scdl.Implementation;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * @version $Rev: 5274 $ $Date: 2008-08-26 05:14:56 +0100 (Tue, 26 Aug 2008) $
 */
public class InstantiationTestCase extends TestCase {
    public static final URI PARENT_URI = URI.create("sca4j://domain/parent");
    public static final URI COMPONENT_BASE = URI.create("sca4j://domain/parent/component");
    public static final String COMPONENT_URI = PARENT_URI.toString() + "/component";
    public static final String CHILD_URI = COMPONENT_URI + "/child";
    public static final String SERVICE_URI = COMPONENT_URI + "#service";
    public static final String REFERENCE_URI = COMPONENT_URI + "#reference";

    private LogicalModelInstantiator logicalModelInstantiator;
    private LogicalCompositeComponent parent;

    public void testInstantiateChildren() throws Exception {
        ComponentDefinition<?> definition = createParentWithChild();
        Composite composite = new Composite(null);
        composite.add(definition);
        logicalModelInstantiator.include(parent, composite);
        LogicalCompositeComponent logicalComponent = (LogicalCompositeComponent) parent.getComponents().iterator().next();
        assertEquals(COMPONENT_URI, logicalComponent.getUri().toString());
        LogicalComponent<?> logicalChild = logicalComponent.getComponent(URI.create(CHILD_URI));
        assertEquals(CHILD_URI, logicalChild.getUri().toString());
    }

    public void testInstantiateServiceReference() throws Exception {
        ComponentDefinition<?> definition = createParentWithServiceAndReference();
        Composite composite = new Composite(null);
        composite.add(definition);
        logicalModelInstantiator.include(parent, composite);
        LogicalCompositeComponent logicalComponent = (LogicalCompositeComponent) parent.getComponents().iterator().next();
        LogicalService logicalService = logicalComponent.getService("service");
        assertEquals(SERVICE_URI, logicalService.getUri().toString());
        LogicalReference logicalReference = logicalComponent.getReference("reference");
        assertEquals(REFERENCE_URI, logicalReference.getUri().toString());
    }


    protected void setUp() throws Exception {
        super.setUp();

        AtomicComponentInstantiator atomicComponentInstantiator = new AtomicComponentInstantiator(null);
        WireInstantiator wireInstantiator = new WireInstantiatorImpl();
        CompositeComponentInstantiator compositeComponentInstantiator =
                new CompositeComponentInstantiator(atomicComponentInstantiator, wireInstantiator, null);
        ResolutionService resolutionService = EasyMock.createMock(ResolutionService.class);
        PromotionNormalizer normalizer = EasyMock.createMock(PromotionNormalizer.class);

        logicalModelInstantiator =
                new LogicalModelInstantiatorImpl(resolutionService,
                                                 normalizer,
                                                 null,
                                                 atomicComponentInstantiator,
                                                 compositeComponentInstantiator,
                                                 wireInstantiator);
        parent = new LogicalCompositeComponent(PARENT_URI, null, null, null);
    }

    private ComponentDefinition<?> createParentWithChild() {
        ComponentType childType = new ComponentType();
        MockImplementation childImp = new MockImplementation();
        childImp.setComponentType(childType);
        ComponentDefinition<MockImplementation> child =
                new ComponentDefinition<MockImplementation>("child");
        child.setImplementation(childImp);

        Composite type = new Composite(null);
        type.add(child);
        CompositeImplementation implementation = new CompositeImplementation();
        implementation.setComponentType(type);
        ComponentDefinition<CompositeImplementation> definition =
                new ComponentDefinition<CompositeImplementation>("component");
        definition.setImplementation(implementation);
        return definition;

    }

    private ComponentDefinition<?> createParentWithServiceAndReference() {
        CompositeService service = new CompositeService("service", null, null);
        List<URI> references = Collections.emptyList();
        CompositeReference reference = new CompositeReference("reference", references);
        Composite type = new Composite(null);
        type.add(service);
        type.add(reference);
        CompositeImplementation implementation = new CompositeImplementation();
        implementation.setComponentType(type);
        ComponentDefinition<CompositeImplementation> definition =
                new ComponentDefinition<CompositeImplementation>("component");
        definition.setImplementation(implementation);
        return definition;

    }

    private class MockImplementation extends Implementation<AbstractComponentType<?, ?, ?, ?>> {
        public QName getType() {
            return null;
        }
    }

}
