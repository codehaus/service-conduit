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
package org.sca4j.fabric.services.messaging;

import java.net.URI;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.spi.services.messaging.MessagingException;
import org.sca4j.spi.services.messaging.MessagingService;

/**
 * A messaging service implementation fr a single node runtime
 *
 * @version $Rev: 2373 $ $Date: 2007-12-28 04:39:37 +0000 (Fri, 28 Dec 2007) $
 */
public class NullMessagingService implements MessagingService {

    public String getScheme() {
        return null;
    }

    public void sendMessage(URI runtimeId, XMLStreamReader content) throws MessagingException {
    }

}
