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
package org.sca4j.fabric.instantiator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.fabric.instantiator.promotion.DefaultPromotionResolutionService;
import org.sca4j.fabric.instantiator.promotion.PromotionResolutionService;
import org.sca4j.fabric.instantiator.target.ExplicitTargetResolutionService;
import org.sca4j.fabric.instantiator.target.ServiceContractResolver;
import org.sca4j.fabric.instantiator.target.ServiceContractResolverImpl;
import org.sca4j.fabric.instantiator.target.TargetResolutionService;
import org.sca4j.fabric.instantiator.target.TypeBasedAutowireResolutionService;
import org.sca4j.introspection.impl.contract.JavaServiceContract;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Multiplicity;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * @version $Rev: 2852 $ $Date: 2008-02-21 23:24:40 +0000 (Thu, 21 Feb 2008) $
 */
public class ResolutionServiceImplTestCase extends TestCase {
    private static final URI REFERENCE_URI = URI.create("source#ref");
    private static final URI SOURCE_URI = URI.create("source");
    private static final URI TARGET_URI = URI.create("target#service");
    private LogicalCompositeComponent domain;
    private ResolutionServiceImpl resolutionService;

    public void testAutowireAtomicToAtomic() throws Exception {
        LogicalCompositeComponent composite = createWiredComposite(domain, Foo.class, Foo.class);
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(composite, change);
        LogicalComponent<?> source = composite.getComponent(SOURCE_URI);
        assertEquals(TARGET_URI, source.getReference("ref").getWires().iterator().next().getTargetUri());
    }

    public void testAutowireAtomicToAtomicRequiresSuperInterface() throws Exception {
        LogicalCompositeComponent composite = createWiredComposite(domain, SuperFoo.class, Foo.class);
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(composite, change);
        LogicalComponent<?> source = composite.getComponent(SOURCE_URI);
        resolutionService.resolve(composite, change);
        assertEquals(TARGET_URI, source.getReference("ref").getWires().iterator().next().getTargetUri());
    }

    public void testAutowireAtomicToAtomicRequiresSubInterface() throws Exception {
        LogicalComponent<CompositeImplementation> composite = createWiredComposite(domain, Foo.class, SuperFoo.class);
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(composite, change);
        assertTrue(change.getErrors().get(0) instanceof ReferenceNotFound);
    }

    public void testAutowireAtomicToAtomicIncompatibleInterfaces() throws Exception {
        LogicalComponent<CompositeImplementation> composite = createWiredComposite(domain, Foo.class, String.class);
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(composite, change);
        assertTrue(change.getErrors().get(0) instanceof ReferenceNotFound);
    }

    public void testNestedAutowireAtomicToAtomic() throws Exception {
        LogicalCompositeComponent composite = createWiredComposite(domain, Foo.class, Foo.class);
        LogicalCompositeComponent parent = createComposite("parent", composite);
        parent.addComponent(composite);
        parent.getDefinition().getImplementation().getComponentType().add(composite.getDefinition());
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(parent, change);
        LogicalComponent<?> source = composite.getComponent(SOURCE_URI);
        assertEquals(TARGET_URI, source.getReference("ref").getWires().iterator().next().getTargetUri());
    }

    public void testAutowireIncludeInComposite() throws Exception {
        LogicalCompositeComponent composite = createComposite("composite", domain);
        LogicalComponent<?> source = createSourceAtomic(Foo.class, composite);
        composite.addComponent(source);
        LogicalComponent<?> target = createTargetAtomic(Foo.class, composite);
        composite.addComponent(target);
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(source, change);
    }

    public void testAutowireToSiblingIncludeInComposite() throws Exception {
        LogicalCompositeComponent parent = createComposite("parent", null);
        LogicalCompositeComponent composite = createComposite("composite", parent);
        LogicalComponent<?> source = createSourceAtomic(Foo.class, composite);
        composite.addComponent(source);
        LogicalComponent<?> target = createTargetAtomic(Foo.class, composite);
        parent.addComponent(composite);
        composite.addComponent(target);
        LogicalChange change = new LogicalChange(domain);
        resolutionService.resolve(composite, change);
    }


    protected void setUp() throws Exception {
        super.setUp();
        PromotionResolutionService promotionResolutionService = new DefaultPromotionResolutionService();
        List<TargetResolutionService> targetResolutionServices = new ArrayList<TargetResolutionService>();
        ServiceContractResolver serviceContractResolver = new ServiceContractResolverImpl();
        ExplicitTargetResolutionService resolutionService = new ExplicitTargetResolutionService(serviceContractResolver);
        targetResolutionServices.add(resolutionService);
        TypeBasedAutowireResolutionService autowireResolutionService = new TypeBasedAutowireResolutionService(serviceContractResolver);
        targetResolutionServices.add(autowireResolutionService);
        this.resolutionService = new ResolutionServiceImpl(promotionResolutionService, targetResolutionServices);
        URI domainUri = URI.create("sca4j://runtime");
        URI runtimeUri = URI.create("runtime");
        domain = new LogicalCompositeComponent(domainUri, runtimeUri, null, null);
    }

    private LogicalCompositeComponent createWiredComposite(LogicalCompositeComponent parent,
                                                           Class<?> sourceClass,
                                                           Class<?> targetClass) {
        LogicalCompositeComponent composite = createComposite("composite", parent);
        LogicalComponent<?> source = createSourceAtomic(sourceClass, composite);
        composite.addComponent(source);
        Composite type = composite.getDefinition().getImplementation().getComponentType();
        type.add(source.getDefinition());
        LogicalComponent<?> target = createTargetAtomic(targetClass, composite);
        composite.addComponent(target);
        type.add(target.getDefinition());
        return composite;
    }

    private LogicalCompositeComponent createComposite(String uri, LogicalCompositeComponent parent) {
        URI parentUri = URI.create(uri);
        Composite type = new Composite(null);
        CompositeImplementation impl = new CompositeImplementation();
        impl.setComponentType(type);
        ComponentDefinition<CompositeImplementation> definition =
                new ComponentDefinition<CompositeImplementation>(parentUri.toString());
        definition.setImplementation(impl);
        URI id = URI.create("runtime");
        return new LogicalCompositeComponent(parentUri, id, definition, parent);
    }

    private LogicalComponent<?> createSourceAtomic(Class<?> requiredInterface, LogicalCompositeComponent parent) {

        ServiceContract contract = new JavaServiceContract(requiredInterface);
        ReferenceDefinition referenceDefinition = new ReferenceDefinition("ref", contract, Multiplicity.ONE_ONE);
        MockComponentType type = new MockComponentType();
        type.add(referenceDefinition);
        MockAtomicImpl impl = new MockAtomicImpl();
        impl.setComponentType(type);
        ComponentDefinition<MockAtomicImpl> definition =
                new ComponentDefinition<MockAtomicImpl>(SOURCE_URI.toString());
        definition.setImplementation(impl);
        ComponentReference target = new ComponentReference(REFERENCE_URI.getFragment());
        target.setAutowire(true);
        definition.add(target);
        URI id = URI.create("runtime");
        LogicalComponent<?> component =
                new LogicalComponent<MockAtomicImpl>(SOURCE_URI, id, definition, parent);
        LogicalReference logicalReference = new LogicalReference(REFERENCE_URI, referenceDefinition, component);
        component.addReference(logicalReference);
        return component;
    }

    private LogicalComponent<?> createTargetAtomic(Class<?> serviceInterface, LogicalCompositeComponent parent) {
        URI uri = URI.create("target");
        JavaServiceContract contract = new JavaServiceContract(serviceInterface);
        ServiceDefinition service = new ServiceDefinition("service", contract);
        MockComponentType type = new MockComponentType();
        type.add(service);
        MockAtomicImpl impl = new MockAtomicImpl();
        impl.setComponentType(type);
        ComponentDefinition<MockAtomicImpl> definition = new ComponentDefinition<MockAtomicImpl>(uri.toString());
        definition.setImplementation(impl);
        URI id = URI.create("runtime");
        LogicalComponent component = new LogicalComponent<MockAtomicImpl>(uri, id, definition, parent);
        LogicalService logicalService = new LogicalService(TARGET_URI, service, parent);
        component.addService(logicalService);
        return component;
    }

    private class MockAtomicImpl extends Implementation<MockComponentType> {
        public QName getType() {
            throw new UnsupportedOperationException();
        }
    }

    private class MockComponentType extends AbstractComponentType<ServiceDefinition, ReferenceDefinition, Property, ResourceDefinition> {

    }

    private interface SuperFoo {

    }

    private interface Foo extends SuperFoo {

    }

}
