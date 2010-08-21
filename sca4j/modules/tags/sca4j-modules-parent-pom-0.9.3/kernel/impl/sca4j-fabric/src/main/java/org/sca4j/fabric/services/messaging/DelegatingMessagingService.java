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
package org.sca4j.fabric.services.messaging;

import java.net.URI;

import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.spi.services.messaging.MessagingException;
import org.sca4j.spi.services.messaging.MessagingService;
import org.sca4j.spi.services.messaging.MessagingServiceRegistry;

/**
 * MessagingService implementation that deleagtes to other MessagingServices. Messages will be dispatched based on the runtime id scheme.
 *
 * @version $Rev: 2964 $ $Date: 2008-02-29 17:44:28 +0000 (Fri, 29 Feb 2008) $
 */
@EagerInit

public class DelegatingMessagingService implements MessagingService {
    private MessagingServiceRegistry registry;

    public DelegatingMessagingService(@Reference MessagingServiceRegistry registry) {
        this.registry = registry;
    }

    public String getScheme() {
        return null;
    }

    public void sendMessage(URI runtimeId, XMLStreamReader content) throws MessagingException {
        // FIXME selectively dispatch message based on a runtime scheme
        String scheme = runtimeId.getScheme();
        MessagingService service = registry.getServiceForScheme(scheme);
        if (service == null) {
            throw new UnknownMessagingSchemeException("No messaging service for scheme: " + scheme, scheme);
        }
        service.sendMessage(runtimeId, content);
    }

}
