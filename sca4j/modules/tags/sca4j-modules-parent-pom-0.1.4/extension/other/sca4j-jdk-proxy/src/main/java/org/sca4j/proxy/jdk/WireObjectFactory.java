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
package org.sca4j.proxy.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Creates a proxy for a wire that implements a provided interface
 *
 * @version $Rev: 2982 $ $Date: 2008-03-02 09:42:03 -0800 (Sun, 02 Mar 2008) $
 */
public class WireObjectFactory<T> implements ObjectFactory<T> {
    private Class<T> interfaze;
    private InteractionType type;
    private String callbackUri;
    private ProxyService proxyService;
    // the cache of proxy interface method to operation mappings
    private Map<Method, InvocationChain> mappings;

    /**
     * Constructor.
     *
     * @param interfaze    the interface to inject on the client
     * @param type         if the wire is stateless, conversational or propagates a conversational context
     * @param callbackUri  the callback URI for the wire or null if the wire is unidirectional
     * @param proxyService the wire service to create the proxy
     * @param mappings     proxy method to wire invocation chain mappings
     * @throws NoMethodForOperationException if a method matching the operation cannot be found
     */
    public WireObjectFactory(Class<T> interfaze,
                             InteractionType type,
                             String callbackUri,
                             ProxyService proxyService,
                             Map<Method, InvocationChain> mappings) throws NoMethodForOperationException {
        this.interfaze = interfaze;
        this.type = type;
        this.callbackUri = callbackUri;
        this.proxyService = proxyService;
        this.mappings = mappings;
    }


    public T getInstance() throws ObjectCreationException {
        return interfaze.cast(proxyService.createProxy(interfaze, type, callbackUri, mappings));
    }
}

