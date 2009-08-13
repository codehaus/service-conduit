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

import javax.servlet.ServletContext;

import org.sca4j.pojo.reflection.Injector;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;

/**
 * Injects objects (reference proxies, properties, contexts) into a ServletContext.
 *
 * @version $Revision$ $Date$
 */
public class ServletContextInjector implements Injector<ServletContext> {
    private ObjectFactory<?> objectFactory;
    private String key;

    public void inject(ServletContext context) throws ObjectCreationException {
        context.setAttribute(key, objectFactory.getInstance());
    }

    public void setObjectFactory(ObjectFactory<?> objectFactory, Object key) {
        this.objectFactory = objectFactory;
        this.key = key.toString();
    }
}
