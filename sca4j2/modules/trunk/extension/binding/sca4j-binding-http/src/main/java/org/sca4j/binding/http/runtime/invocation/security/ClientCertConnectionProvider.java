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
package org.sca4j.binding.http.runtime.invocation.security;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.http.provision.security.ClientCertAuthenticationPolicy;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

public class ClientCertConnectionProvider implements ConnectionProvider<ClientCertAuthenticationPolicy> {
  
    @Reference protected ClassLoaderRegistry classLoaderRegistry;

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
                ClassLoader classLoader = classLoaderRegistry.getClassLoader(classLoaderId);
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
