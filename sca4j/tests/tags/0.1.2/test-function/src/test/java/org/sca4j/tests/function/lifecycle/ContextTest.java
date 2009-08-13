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
package org.sca4j.tests.function.lifecycle;

import junit.framework.TestCase;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev: 2683 $ $Date: 2008-02-03 11:44:34 +0000 (Sun, 03 Feb 2008) $
 */
public class ContextTest extends TestCase {

    @Context
    protected RequestContext requestContext;

    @Context
    protected ComponentContext componentContext;

    @Property
    protected String uri;

    public void testRequestContext() {
        assertNotNull(requestContext);
        assertSame(requestContext, componentContext.getRequestContext());
    }

    public void testComponentContext() {
        assertNotNull(componentContext);
        assertEquals(uri, componentContext.getURI());
    }
}
