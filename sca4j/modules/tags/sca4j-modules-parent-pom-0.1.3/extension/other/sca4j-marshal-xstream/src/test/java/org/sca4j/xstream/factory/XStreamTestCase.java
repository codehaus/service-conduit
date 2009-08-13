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
package org.sca4j.xstream.factory;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

/**
 * @version $Rev: 4842 $ $Date: 2008-06-20 11:47:22 +0100 (Fri, 20 Jun 2008) $
 */
public class XStreamTestCase extends TestCase {

    public void testCreate() {
        XStreamFactory factory = new XStreamFactoryImpl(null);
        XStream xstream = factory.createInstance();
        String output = xstream.toXML("hello");
        assertTrue(output.endsWith("<string>hello</string>"));
        assertEquals("hello", xstream.fromXML(output));
    }

}
