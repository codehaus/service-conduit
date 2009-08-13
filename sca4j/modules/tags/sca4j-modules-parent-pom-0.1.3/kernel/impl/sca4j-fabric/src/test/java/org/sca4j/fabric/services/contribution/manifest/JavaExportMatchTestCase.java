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

import junit.framework.TestCase;

/**
 * @version $Rev: 205 $ $Date: 2007-06-09 06:58:21 +0100 (Sat, 09 Jun 2007) $
 */
public class JavaExportMatchTestCase extends TestCase {

    public void testPackageMultiLevelMatch() {
        JavaExport jexport = new JavaExport("com.foo");
        JavaImport jimport = new JavaImport("com.foo.bar.Baz");
        assertEquals(JavaExport.EXACT_MATCH, jexport.match(jimport));
    }

    public void testNoSubPackageMatch() {
        JavaExport jexport = new JavaExport("com.foo.bar");
        JavaImport jimport = new JavaImport("com.foo");
        assertEquals(JavaExport.NO_MATCH, jexport.match(jimport));
    }

}
