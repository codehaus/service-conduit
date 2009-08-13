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
package tests.rs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.UriBuilder;
import junit.framework.TestCase;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev: 5444 $ $Date: 2008-09-19 15:57:03 +0100 (Fri, 19 Sep 2008) $
 */
public class TestClient extends TestCase {
    
    @Property protected String hostURI;

    public TestClient() {
    }

    public void testEcho() {
        UriBuilder uri =UriBuilder.fromUri(hostURI).path("echo");
        WebResource resource = Client.create().resource(uri.path("Hello").build());
        assertEquals("Hello World", resource.post(String.class,"World"));
    }
   
}
