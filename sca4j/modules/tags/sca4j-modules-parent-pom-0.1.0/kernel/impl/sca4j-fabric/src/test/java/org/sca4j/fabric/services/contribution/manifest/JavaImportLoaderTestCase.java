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
package org.sca4j.fabric.services.contribution.manifest;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev: 4226 $ $Date: 2008-05-15 10:38:18 +0100 (Thu, 15 May 2008) $
 */
public class JavaImportLoaderTestCase extends TestCase {
    private JavaImportLoader loader = new JavaImportLoader();
    private XMLStreamReader reader;

    public void testRead() throws Exception {
        JavaImport jimport = loader.load(reader, null);
        assertEquals("foo.bar.baz", jimport.getPackageName());
    }


    protected void setUp() throws Exception {
        super.setUp();
        reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "package")).andReturn("foo.bar.baz");
        EasyMock.replay(reader);
    }
}
