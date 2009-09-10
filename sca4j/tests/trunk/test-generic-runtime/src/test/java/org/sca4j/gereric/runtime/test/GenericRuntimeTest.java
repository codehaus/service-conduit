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
package org.sca4j.gereric.runtime.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.stream.XMLStreamException;

import org.sca4j.generic.runtime.test.HelloWorld;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.StartException;
import org.sca4j.runtime.generic.junit.AbstractScaTest;

public class GenericRuntimeTest extends AbstractScaTest {
    
    public GenericRuntimeTest() throws IOException, InitializationException, ContributionException, StartException, URISyntaxException, DeploymentException, XMLStreamException {
        super("META-INF/hello.composite");
        // TODO Auto-generated constructor stub
    }

    public void test() throws Exception {
        
        HelloWorld helloWorld = genericRuntime.getServiceProxy(HelloWorld.class, URI.create("test/HelloWorld"));
        assertEquals("Hello, Meeraj", helloWorld.sayHello("Meeraj"));
        
    }

}
