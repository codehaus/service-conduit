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
public class SCA4JSocketFactory extends DefaultSocketFactory {
    
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
        socket.connect(socketAddress);
        return socket;
    }

}
