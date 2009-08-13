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

import java.util.HashMap;
import java.util.Map;

import org.sca4j.spi.services.messaging.MessagingService;
import org.sca4j.spi.services.messaging.MessagingServiceRegistry;

/**
 * Default MessagingServiceRegistry implementation.
 *
 * @version $Rev: 2373 $ $Date: 2007-12-28 04:39:37 +0000 (Fri, 28 Dec 2007) $
 */
public class MessagingServiceRegistryImpl implements MessagingServiceRegistry {
    private Map<String, MessagingService> services = new HashMap<String, MessagingService>();

    public void register(MessagingService service) {
        services.put(service.getScheme(), service);
    }

    public void unRegister(MessagingService service) {
        services.remove(service.getScheme());
    }

    public MessagingService getServiceForScheme(String scheme) {
        return services.get(scheme);
    }
}
