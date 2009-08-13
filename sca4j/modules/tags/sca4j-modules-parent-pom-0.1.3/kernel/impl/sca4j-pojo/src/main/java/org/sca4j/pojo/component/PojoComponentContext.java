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

import org.osoa.sca.ComponentContext;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.RequestContext;

/**
 * @version $Rev: 5243 $ $Date: 2008-08-20 22:14:37 +0100 (Wed, 20 Aug 2008) $
 */
public class PojoComponentContext implements ComponentContext {
    private final PojoComponent<?> component;
    private final PojoRequestContext requestContext;

    public PojoComponentContext(PojoComponent<?> component, PojoRequestContext requestContext) {
        this.component = component;
        this.requestContext = requestContext;
    }

    public String getURI() {
        return component.getUri().toString();
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return null;
    }

    public <B> B getService(Class<B> businessInterface, String referenceName) {
        return null;
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
        return null;
    }

    public <B> B getProperty(Class<B> type, String propertyName) {
        return null;
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface) {
        return null;
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, String serviceName) {
        return null;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }
}
