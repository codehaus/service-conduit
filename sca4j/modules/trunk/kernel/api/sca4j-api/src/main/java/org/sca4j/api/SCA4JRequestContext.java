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

package org.sca4j.api;

import org.osoa.sca.RequestContext;

/**
 * A SCA4J extension to the SCA RequestContext API. Components may reference this interface when for fields or setters marked with @Context instead
 * of the SCA RequestContext variant. For example:
 * <pre>
 * public class SomeComponnent implements SomeService {
 *      &#064;Context
 *      protected F3RequestContext context;
 *      //...
 * }
 * </pre>
 * At runtime, the <code>context</code> field will be injected with an instance of F3RequestContext.
 *
 * @version $Revision$ $Date$
 */
public interface SCA4JRequestContext extends RequestContext {

    /**
     * Returns the header value corresponding to a name for the current request message.
     *
     * @param type the value type
     * @param name the header name
     * @return the header value or null if not found
     */
    <T> T getHeader(Class<T> type, String name);

    /**
     * Sets a header value for the current request context. Headers will be propagated across threads for non-blocking invocations made by a component
     * when processing a request. However, headers propagation across process boundaries is binding-specific. Some bindings may propagate headers
     * while others may ignore them.
     * <p/>
     * Note that header values should be immutable since, unlike purely synchronous programming models, SCA's asynchronous model may result in
     * multiple threads simultaneously accessing a header. For example, two non-blocking invocations to local services may access the same header.
     *
     * @param name  the header name
     * @param value the header value
     */
    void setHeader(String name, Object value);

    /**
     * Clears a header for the current request context.
     *
     * @param name Name of the header to be removed.
     */
    void removeHeader(String name);

}
