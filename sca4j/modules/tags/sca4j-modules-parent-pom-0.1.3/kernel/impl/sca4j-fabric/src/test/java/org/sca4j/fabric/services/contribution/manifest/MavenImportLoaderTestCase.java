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

import org.sca4j.spi.services.contribution.MavenImport;

/**
 * @version $Rev: 5137 $ $Date: 2008-08-02 08:54:51 +0100 (Sat, 02 Aug 2008) $
 */
public class MavenImportLoaderTestCase extends TestCase {
    private MavenImportLoader loader = new MavenImportLoader();
    private XMLStreamReader reader;

    public void testRead() throws Exception {
        MavenImport imprt = loader.load(reader, null);
        assertEquals("foo", imprt.getGroupId());
        assertEquals("bar", imprt.getArtifactId());
        assertEquals("1.0-SNAPSHOT", imprt.getVersion());
        assertEquals("zip", imprt.getClassifier());
    }


    protected void setUp() throws Exception {
        super.setUp();
        reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "groupId")).andReturn("foo");
        EasyMock.expect(reader.getAttributeValue(null, "artifactId")).andReturn("bar");
        EasyMock.expect(reader.getAttributeValue(null, "version")).andReturn("1.0-SNAPSHOT");
        EasyMock.expect(reader.getAttributeValue(null, "classifier")).andReturn("zip");
        EasyMock.replay(reader);
    }
}
