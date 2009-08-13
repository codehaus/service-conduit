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
package org.sca4j.web.runtime;

import javax.servlet.http.HttpSession;

import org.sca4j.pojo.reflection.Injector;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;

/**
 * Injects an instance (e.g. a reference proxy) into an HTTP session object.
 *
 * @version $Revision$ $Date$
 */
public class HttpSessionInjector implements Injector<HttpSession> {
    private ObjectFactory<?> objectFactory;
    private String name;

    public void inject(HttpSession session) throws ObjectCreationException {
        session.setAttribute(name, objectFactory.getInstance());
    }

    public void setObjectFactory(ObjectFactory<?> objectFactory, Object name) {
        this.objectFactory = objectFactory;
        this.name = name.toString();
    }
}
