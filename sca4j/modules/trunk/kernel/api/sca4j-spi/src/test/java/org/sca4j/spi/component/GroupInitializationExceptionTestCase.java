/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
