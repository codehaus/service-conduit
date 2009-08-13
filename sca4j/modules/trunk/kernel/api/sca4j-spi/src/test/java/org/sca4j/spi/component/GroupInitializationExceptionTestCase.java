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
package org.sca4j.spi.component;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @version $Rev: 3566 $ $Date: 2008-04-05 02:26:37 +0100 (Sat, 05 Apr 2008) $
 */
public class GroupInitializationExceptionTestCase extends TestCase {
    private PrintWriter writer;
    private PrintStream printer;
    private Exception cause1;
    private Exception cause2;
    private List<Exception> causes;
    private GroupInitializationException e;

    public void testCauses() {
        assertTrue(e.getCauses().contains(cause1));
        assertTrue(e.getCauses().contains(cause2));
    }
    
    // commented out to prevent confusing stack traces in the build log - uncomment to verify output
/*
    public void testPrintStackTraceToWriter() {
        e.printStackTrace(writer);
    }

    public void testPrintStackTraceToStream() {
        e.printStackTrace(printer);
    }

    public void testPrintStackTrace() {
        e.printStackTrace();
    }
*/

    protected void setUp() throws Exception {
        super.setUp();
        cause1 = new Exception("An Exception", new Exception("Nested Cause"));
        cause2 = new RuntimeException("A RuntimeException");
        causes = new ArrayList<Exception>();
        causes.add(cause1);
        causes.add(cause2);
        writer = new PrintWriter(System.err);
        printer = new PrintStream(System.err);
        e = new GroupInitializationException(causes);
    }
}
