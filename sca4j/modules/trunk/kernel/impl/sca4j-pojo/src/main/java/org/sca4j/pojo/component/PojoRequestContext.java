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
package org.sca4j.pojo.component;

import javax.security.auth.Subject;

import org.osoa.sca.CallableReference;

import org.sca4j.api.SCA4JRequestContext;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 5455 $ $Date: 2008-09-21 06:26:43 +0100 (Sun, 21 Sep 2008) $
 */
public class PojoRequestContext implements SCA4JRequestContext {
    public Subject getSecuritySubject() {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        return workContext.getSubject();
    }

    public String getServiceName() {
        return null;
    }

    public <B> CallableReference<B> getServiceReference() {
        return null;
    }

    public <CB> CB getCallback() {
        return null;
    }

    public <CB> CallableReference<CB> getCallbackReference() {
        return null;
    }

    public <T> T getHeader(Class<T> type, String name) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        return workContext.getHeader(type, name);
    }

    public void setHeader(String name, Object value) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        workContext.setHeader(name, value);
    }

    public void removeHeader(String name) {
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        workContext.removeHeader(name);
    }
}
