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
package org.sca4j.fabric.async;

import java.util.List;
import java.util.Map;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Encapsulates an invocation to be processed asynchronously.
 *
 * @version $Revision$ $Date$
 */
public class AsyncRequest extends DefaultPausableWork {
    private final Interceptor next;
    private final Message message;
    private List<CallFrame> stack;
    private Map<String, Object> headers;

    public AsyncRequest(Interceptor next, Message message, List<CallFrame> stack, Map<String, Object> headers) {
        this.next = next;
        this.message = message;
        this.stack = stack;
        this.headers = headers;
    }

    public void execute() {
        WorkContext newWorkContext = new WorkContext();
        if (stack != null) {
            newWorkContext.addCallFrames(stack);
        }
        if (headers != null) {
            newWorkContext.addHeaders(headers);
        }
        message.setWorkContext(newWorkContext);
        next.invoke(message);
    }

    public Interceptor getNext() {
        return next;
    }

    public Message getMessage() {
        return message;
    }
}
