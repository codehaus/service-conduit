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
package org.sca4j.fabric.builder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.spi.builder.interceptor.InterceptorBuilder;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 3126 $ $Date: 2008-03-16 20:16:42 +0000 (Sun, 16 Mar 2008) $
 */
public class ConnectorImplTestCase extends TestCase {
    private ConnectorImpl connector;
    private PhysicalWireDefinition definition;
    private PhysicalOperationDefinition operation;
    private PhysicalOperationDefinition callback;
    private Map<Class<? extends PhysicalInterceptorDefinition>, InterceptorBuilder<?, ?>> builders;

    public void testCreateWire() throws Exception {
        Wire wire = connector.createWire(definition);
        assertEquals(2, wire.getInvocationChains().size());
    }

    public void testDispatchToBuilder() throws Exception {
        InterceptorBuilder builder = EasyMock.createMock(InterceptorBuilder.class);
        EasyMock.expect(builder.build(EasyMock.isA(PhysicalInterceptorDefinition.class))).andReturn(null).times(2);
        EasyMock.replay(builder);
        builders.put(PhysicalInterceptorDefinition.class, builder);

        PhysicalInterceptorDefinition interceptorDefinition = new PhysicalInterceptorDefinition();
        operation.addInterceptor(interceptorDefinition);
        callback.addInterceptor(interceptorDefinition);

        connector.createWire(definition);
        EasyMock.verify(builder);
    }

    protected void setUp() throws Exception {
        super.setUp();
        connector = new ConnectorImpl();
        builders = new HashMap<Class<? extends PhysicalInterceptorDefinition>, InterceptorBuilder<?, ?>>();
        connector.setInterceptorBuilders(builders);

        PhysicalWireSourceDefinition sourceDefinition = new PhysicalWireSourceDefinition();
        sourceDefinition.setUri(URI.create("source"));
        PhysicalWireTargetDefinition targetDefinition = new PhysicalWireTargetDefinition();
        targetDefinition.setUri(URI.create("target"));
        definition = new PhysicalWireDefinition(sourceDefinition, targetDefinition);
        operation = new PhysicalOperationDefinition();
        operation.setName("operation");
        definition.addOperation(operation);
        callback = new PhysicalOperationDefinition();
        callback.setName("callback");
        callback.setCallback(true);
        definition.addOperation(callback);
    }
}
