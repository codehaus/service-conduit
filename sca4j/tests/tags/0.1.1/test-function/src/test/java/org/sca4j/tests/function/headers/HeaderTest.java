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

package org.sca4j.tests.function.headers;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.SCA4JRequestContext;

/**
 * @version $Revision$ $Date$
 */
public class HeaderTest extends TestCase {

    @Context
    protected SCA4JRequestContext context;

    @Reference
    protected HeaderService headerService;

    @Reference
    protected AsyncHeaderService asyncHeaderService;

    public void testSetHeader() {
        context.setHeader("header", "test");
        headerService.invokeTestHeader();
        context.removeHeader("header");
        headerService.invokeTestHeaderCleared();
    }

    public void testAsyncSetHeader() throws Exception {
        context.setHeader("header", "test");
        HeaderFuture future = new HeaderFuture();
        asyncHeaderService.invokeTestHeader(future);
        AssertionError error = future.get();
        if (error != null) {
            throw error;
        }
        context.removeHeader("header");
        future = new HeaderFuture();
        asyncHeaderService.invokeTestHeaderCleared(future);
        error = future.get();
        if (error != null) {
            throw error;
        }
    }
}
