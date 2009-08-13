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

package org.sca4j.binding.ftp.runtime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.net.DefaultSocketFactory;

/**
 * Overrides the DefaultSocketFactory behavior provided by Apache Commons Net by setting a timeout for opening a socket connection.
 *
 * @version $Revision$ $Date$
 */
public class ExpiringSocketFactory extends DefaultSocketFactory {
    private final int connectTimeout;

    /**
     * Constructor.
     *
     * @param connectTimeout the timeout to wait in milliseconds to open a connection.
     */
    public ExpiringSocketFactory(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    public Socket createSocket(String host, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(host, port);
        return createSocket(address, null);
    }

    public Socket createSocket(InetAddress address, int port) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        return createSocket(socketAddress, null);
    }

    public Socket createSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException {
        if (host != null) {
            return createSocket(new InetSocketAddress(host, port), new InetSocketAddress(localAddr, localPort));
        } else {
            return createSocket(new InetSocketAddress(InetAddress.getByName(null), port), new InetSocketAddress(localAddr, localPort));
        }
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
        if (address != null) {
            return createSocket(new InetSocketAddress(address, port), new InetSocketAddress(localAddr, localPort));
        } else {
            return createSocket(null, new InetSocketAddress(localAddr, localPort));
        }
    }


    private Socket createSocket(InetSocketAddress socketAddress, InetSocketAddress localSocketAddress) throws IOException {
        Socket socket = new Socket();
        if (localSocketAddress != null) {
            socket.bind(localSocketAddress);
        }
        socket.connect(socketAddress, connectTimeout);
        return socket;
    }

}
