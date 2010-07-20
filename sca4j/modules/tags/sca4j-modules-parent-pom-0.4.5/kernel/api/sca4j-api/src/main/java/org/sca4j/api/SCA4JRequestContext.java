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
 * Original Codehaus Header
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
 * Original Apache Header
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
