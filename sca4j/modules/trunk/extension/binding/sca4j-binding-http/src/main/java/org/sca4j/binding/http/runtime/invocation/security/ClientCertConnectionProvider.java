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
package org.sca4j.binding.http.runtime.invocation.security;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.sca4j.binding.http.provision.security.ClientCertAuthenticationPolicy;

public class ClientCertConnectionProvider implements ConnectionProvider<ClientCertAuthenticationPolicy> {

    public HttpClient createClient(ClientCertAuthenticationPolicy authenticationPolicy, URL url, URI classLoaderId) {
        
        try {

            String scheme = url.getProtocol();
            int port = url.getPort();
            URL keyStore = null;
            URL trustStore = null;
            
            String keyStorePassword = authenticationPolicy.getKeyStorePassword();
            String trustStorePassword = authenticationPolicy.getTrustStorePassword();
            
            // TODO Replace this with a custom protocol handler.
            if (authenticationPolicy.isClasspath()) {
                ClassLoader classLoader = getClass().getClassLoader();
                keyStore = classLoader.getResource(authenticationPolicy.getKeyStore());
                trustStore = classLoader.getResource(authenticationPolicy.getTrustStore());
            } else {
                keyStore = new File(authenticationPolicy.getKeyStore()).toURL();
                trustStore = new File(authenticationPolicy.getTrustStore()).toURL();
            }
            
            ProtocolSocketFactory protocolSocketFactory = new AuthSSLProtocolSocketFactory(keyStore, keyStorePassword, trustStore, trustStorePassword);
            Protocol authHttps = new Protocol(scheme, protocolSocketFactory, port);
            
            HostConfiguration hostConfig = new HostConfiguration();
			hostConfig.setHost(url.getHost(), port, authHttps);
			SCA4jHttpConnectionManager httpConnMgr = new SCA4jHttpConnectionManager(hostConfig);
                        
            HttpClient httpClient = new HttpClient(httpConnMgr);
            httpClient.getParams().setConnectionManagerClass(SCA4jHttpConnectionManager.class);
            
            return httpClient;
            
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        
    }

}
