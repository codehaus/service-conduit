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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.binding.ftp.runtime;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.UnknownHostException;

import org.apache.commons.net.SocketFactory;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.ftp.provision.FtpSecurity;
import org.sca4j.binding.ftp.provision.FtpWireTargetDefinition;
import org.sca4j.host.expression.ExpressionExpansionException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Revision$ $Date$
 */
public class FtpTargetWireAttacher implements TargetWireAttacher<FtpWireTargetDefinition> {
    
    private FtpInterceptorMonitor monitor;

    public FtpTargetWireAttacher(@Monitor FtpInterceptorMonitor monitor) {
        this.monitor = monitor;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, FtpWireTargetDefinition target, Wire wire) throws WiringException {

        try {

            URI uri = URI.create(URLDecoder.decode(target.getUri().toString(), "UTF-8"));
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 23 : uri.getPort();
            InetAddress hostAddress = "localhost".equals(host) ? InetAddress.getLocalHost() : InetAddress.getByName(host);
            
            String remotePath = uri.getPath();
            String tmpFileSuffix = target.getTmpFileSuffix();

            FtpSecurity security = expandFtpSecurity(target.getSecurity());            
            boolean active = target.isActive();
            SocketFactory factory = new SCA4JSocketFactory();
            
            for (InvocationChain invocationChain : wire.getInvocationChains().values()) {
                FtpTargetInterceptor targetInterceptor =
                        new FtpTargetInterceptor(hostAddress, port, security, active, factory, monitor);
                targetInterceptor.setTmpFileSuffix(tmpFileSuffix);
                targetInterceptor.setRemotePath(remotePath);
                
                invocationChain.addInterceptor(targetInterceptor);
            }
        } catch (UnknownHostException e) {
            throw new WiringException(e);
        } catch (UnsupportedEncodingException e) {
            throw new WiringException(e);
        }

    }

    public void detachFromTarget(PhysicalWireSourceDefinition source, FtpWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }

    public ObjectFactory<?> createObjectFactory(FtpWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }
    
    /**
     * Expands the FTP security if it contains an expression of the form ${..}.
     * 
     * @param ftpSecurity FTP security which contains FTP authentication details
     * @return the expanded ftp security
     * @throws WiringException if there is an error expanding an expression
     */
    private FtpSecurity expandFtpSecurity(FtpSecurity ftpSecurity) throws WiringException {
        try {
            return new FtpSecurity(ftpSecurity.getUser(), ftpSecurity.getPassword());
        } catch (ExpressionExpansionException e) {
            throw new WiringException(e);
        }
    }

}
