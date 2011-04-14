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
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.sca4j.binding.http.provision.security.ClientCertAuthenticationPolicy;

public class ClientCertConnectionProvider extends AbstractConnectionProvider<ClientCertAuthenticationPolicy> {

	/**
	 * Builds host configuration data
	 */
	@Override
    HostConfiguration buildHostInfo(ClientCertAuthenticationPolicy authenticationPolicy, URL url) {
		final String keyStorePassword = authenticationPolicy.getKeyStorePassword();
		final String trustStorePassword = authenticationPolicy.getTrustStorePassword();
		final KeyTrustStore keyTrustStore = createKeyAndTrustStore(authenticationPolicy);

		HostConfiguration hostConfig = new HostConfiguration();
		hostConfig.setHost(url.getHost(), url.getPort(), createAuthProtocol(keyTrustStore.keyStore, keyTrustStore.trustStore,
				                                                            keyStorePassword, trustStorePassword, url));
		return hostConfig;
	}

	/*
	 * Assigns Key and Trust Store
	 */
	private KeyTrustStore createKeyAndTrustStore(ClientCertAuthenticationPolicy authPolicy) {

		final KeyTrustStore keyTrustStore = new KeyTrustStore();

		try {
			if (authPolicy.isClasspath()) {
				ClassLoader classLoader = getClass().getClassLoader();
				keyTrustStore.keyStore = classLoader.getResource(authPolicy.getKeyStore());
				keyTrustStore.trustStore = classLoader.getResource(authPolicy.getTrustStore());
			} else {
				keyTrustStore.keyStore = getURLFromFileLocation(new File(authPolicy.getKeyStore()));
				keyTrustStore.trustStore = getURLFromFileLocation(new File(authPolicy.getTrustStore()));
			}
		} catch(MalformedURLException me){
			throw new AssertionError(me);
		}

		return keyTrustStore;
	}


	/*
	 * Return the URL location from the file Object, toURI has to be used see deprecation on toURL for File
	 */
	private URL getURLFromFileLocation(File file) throws MalformedURLException {
	    return file.toURI().toURL();
	}

	/*
	 * Creates Authentication Protocol
	 */
	private Protocol createAuthProtocol(URL keyStore, URL trustStore, String keyStorePassword, String trustStorePassword, URL url) {
		final ProtocolSocketFactory protocolSocketFactory = new AuthSSLProtocolSocketFactory(keyStore, keyStorePassword,
				                                                                             trustStore, trustStorePassword);
		final Protocol authHttps = new Protocol(url.getProtocol(), protocolSocketFactory, url.getPort());

		return authHttps;
	}

	/*
	 * URL for Key Stores and Trust Stores
	 */
	private class KeyTrustStore {
		private URL keyStore;
		private URL trustStore;
	}
}
