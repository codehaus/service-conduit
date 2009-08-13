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
package org.sca4j.fabric.services.routing;

import java.net.URI;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import org.sca4j.spi.executor.CommandExecutorRegistry;

/**
 * @version $Rev: 3681 $ $Date: 2008-04-19 19:00:58 +0100 (Sat, 19 Apr 2008) $
 */
public class RuntimeRoutingServiceTestCase extends TestCase {
    public static final URI RUNTIME_ID = URI.create("localhost://runtime");
    private RuntimeRoutingService service;

    public void testRouting() throws Exception {
    }

    protected void setUp() throws Exception {
        super.setUp();
        CommandExecutorRegistry commandRegistry = EasyMock.createMock(CommandExecutorRegistry.class);
        EasyMock.replay(commandRegistry);
        service = new RuntimeRoutingService(commandRegistry, null);
    }
}
