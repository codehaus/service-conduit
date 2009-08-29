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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * <p>
 * AuthSSLProtocolSocketFactory can be used to validate the identity of the
 * HTTPS server against a list of trusted certificates and to authenticate to
 * the HTTPS server using a private key.
 * </p>
 * 
 * <p>
 * AuthSSLProtocolSocketFactory will enable server authentication when supplied
 * with a {@link KeyStore truststore} file containg one or several trusted
 * certificates. The client secure socket will reject the connection during the
 * SSL session handshake if the target HTTPS server attempts to authenticate
 * itself with a non-trusted certificate.
 * </p>
 * 
 * <p>
 * Use JDK keytool utility to import a trusted certificate and generate a
 * truststore file:
 * 
 * <pre>
 *     keytool -import -alias &quot;my server cert&quot; -file server.crt -keystore my.truststore
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * AuthSSLProtocolSocketFactory will enable client authentication when supplied
 * with a {@link KeyStore keystore} file containg a private key/public
 * certificate pair. The client secure socket will use the private key to
 * authenticate itself to the target HTTPS server during the SSL session
 * handshake if requested to do so by the server. The target HTTPS server will
 * in its turn verify the certificate presented by the client in order to
 * establish client's authenticity
 * </p>
 * 
 * <p>
 * Use the following sequence of actions to generate a keystore file
 * </p>
 * <ul>
 * <li>
 * <p>
 * Use JDK keytool utility to generate a new key
 * 
 * <pre>
 * keytool -genkey -v -alias &quot;my client key&quot; -validity 365 -keystore my.keystore
 * </pre>
 * 
 * For simplicity use the same password for the key as that of the keystore
 * </p>
 * </li>
 * <li>
 * <p>
 * Issue a certificate signing request (CSR)
 * 
 * <pre>
 * keytool -certreq -alias &quot;my client key&quot; -file mycertreq.csr -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Send the certificate request to the trusted Certificate Authority for
 * signature. One may choose to act as her own CA and sign the certificate
 * request using a PKI tool, such as OpenSSL.
 * </p>
 * </li>
 * <li>
 * <p>
 * Import the trusted CA root certificate
 * 
 * <pre>
 * keytool -import -alias &quot;my trusted ca&quot; -file caroot.crt -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Import the PKCS#7 file containg the complete certificate chain
 * 
 * <pre>
 * keytool -import -alias &quot;my client key&quot; -file mycert.p7 -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Verify the content the resultant keystore file
 * 
 * <pre>
 * keytool -list -v -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * </ul>
 * <p>
 * Example of using custom protocol socket factory for a specific host:
 * 
 * <pre>
 * Protocol authhttps = new Protocol(&quot;https&quot;, new AuthSSLProtocolSocketFactory(
 *      new URL(&quot;file:my.keystore&quot;), &quot;mypassword&quot;,
 *      new URL(&quot;file:my.truststore&quot;), &quot;mypassword&quot;), 443);
 * HttpClient client = new HttpClient();
 * client.getHostConfiguration().setHost(&quot;localhost&quot;, 443, authhttps);
 * // use relative url only
 * GetMethod httpget = new GetMethod(&quot;/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * <p>
 * Example of using custom protocol socket factory per default instead of the
 * standard one:
 * 
 * <pre>
 * Protocol authhttps = new Protocol(&quot;https&quot;, new AuthSSLProtocolSocketFactory(
 *      new URL(&quot;file:my.keystore&quot;), &quot;mypassword&quot;,
 *      new URL(&quot;file:my.truststore&quot;), &quot;mypassword&quot;), 443);
 * Protocol.registerProtocol(&quot;https&quot;, authhttps);
 * HttpClient client = new HttpClient();
 * GetMethod httpget = new GetMethod(&quot;https://localhost/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * 
 * @author <a href="mailto:oleg -at- ural.ru">Oleg Kalnichevski</a>
 * 
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component. The
 * component is provided as a reference material, which may be inappropriate for
 * use without additional customization.
 * </p>
 */

public class AuthSSLProtocolSocketFactory implements SecureProtocolSocketFactory, ProtocolSocketFactory {

    private URL keystoreUrl = null;
    private String keystorePassword = null;
    private URL truststoreUrl = null;
    private String truststorePassword = null;
    private SSLContext sslcontext = null;
    
    public AuthSSLProtocolSocketFactory(URL keystoreUrl, String keystorePassword, URL truststoreUrl, String truststorePassword) {
        this.keystoreUrl = keystoreUrl;
        this.keystorePassword = keystorePassword;
        this.truststoreUrl = truststoreUrl;
        this.truststorePassword = truststorePassword;
    }

    
    /**
     * @see org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int, org.apache.commons.httpclient.params.HttpConnectionParams)
     */
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory;
        try {
            socketfactory = getSSLContext().getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
        try {
            return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
     */
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        try {
            return getSSLContext().getSocketFactory().createSocket(host, port);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
     */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        try {
            return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private static KeyStore createKeyStore(URL url, String password) throws Exception {
        KeyStore keystore = KeyStore.getInstance("jks");
        InputStream is = null;
        try {
            is = url.openStream();
            keystore.load(is, password != null ? password.toCharArray() : null);
        } finally {
            if (is != null)
                is.close();
        }
        return keystore;
    }

    private static KeyManager[] createKeyManagers(KeyStore keystore, String password) throws Exception {
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password != null ? password.toCharArray() : null);
        return kmfactory.getKeyManagers();
    }

    private static TrustManager[] createTrustManagers(KeyStore keystore) throws Exception {
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(keystore);
        TrustManager[] trustmanagers = tmfactory.getTrustManagers();
        for (int i = 0; i < trustmanagers.length; i++) {
            if (trustmanagers[i] instanceof X509TrustManager) {
                trustmanagers[i] = new AuthSSLX509TrustManager((X509TrustManager) trustmanagers[i]);
            }
        }
        return trustmanagers;
    }

    private SSLContext createSSLContext() throws Exception {
        KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            if (this.keystoreUrl != null) {
                KeyStore keystore = createKeyStore(this.keystoreUrl, this.keystorePassword);
                keymanagers = createKeyManagers(keystore, this.keystorePassword);
            }
            if (this.truststoreUrl != null) {
                KeyStore keystore = createKeyStore(this.truststoreUrl, this.truststorePassword);
                trustmanagers = createTrustManagers(keystore);
            }
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(keymanagers, trustmanagers, null);
            return sslcontext;
    }

    private SSLContext getSSLContext() throws Exception {
        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }
    
}