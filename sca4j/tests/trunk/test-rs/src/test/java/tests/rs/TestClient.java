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

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 5444 $ $Date: 2008-09-19 15:57:03 +0100 (Fri, 19 Sep 2008) $
 */
public class TestClient extends TestCase {
    
    @Reference protected CardService cardService;
    @Reference protected IMocksControl control;
    @Reference protected CardService outboundProxy;
    
    public void testUpload() throws Exception {
    	Thread.sleep(30000);
        control.reset();
        EasyMock.expect(outboundProxy.upload("ABCD", 1234)).andReturn("ABCD uploaded with amount 1234.0");
        control.replay();
        assertEquals("ABCD uploaded with amount 1234.0", cardService.upload("ABCD", 1234));
        control.verify();
    }

    public void testDisable() {
        control.reset();
        EasyMock.expect(outboundProxy.disable("ABCD")).andReturn("ABCD disabled");
        control.replay();
        assertEquals("ABCD disabled", cardService.disable("ABCD"));
        control.verify();
    }
   
}
