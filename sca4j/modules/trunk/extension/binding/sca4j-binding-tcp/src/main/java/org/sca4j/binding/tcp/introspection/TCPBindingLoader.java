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
package org.sca4j.binding.tcp.introspection;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.tcp.scdl.TCPBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * @version $Revision$ $Date$
 */
public class TCPBindingLoader implements TypeLoader<TCPBindingDefinition> {  
    
    private final LoaderHelper loaderHelper;

    /**
     * Constructor.
     *
     * @param loaderHelper the policy helper
     */
    public TCPBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    /**
     * {@inheritDoc}
     */
    public TCPBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext)
            throws XMLStreamException {

        TCPBindingDefinition bd = null;
        String uri = null;

        try {

            uri = reader.getAttributeValue(null, "uri");
            if (uri == null) {
                MissingAttribute failure = new MissingAttribute("A binding URI must be specified ", "uri", reader);
                introspectionContext.addError(failure);
                return null;
            }
            if (!uri.contains("://")) { // Default to TCP.
                uri = "tcp://" + uri;
            }

            bd = new TCPBindingDefinition(new URI(uri), loaderHelper.loadKey(reader));

        } catch (URISyntaxException ex) {
            InvalidValue failure = new InvalidValue("The TCP binding URI is not valid: " + uri, "uri", reader);
            introspectionContext.addError(failure);
        }

        LoaderUtil.skipToEndElement(reader);
        return bd;

    }

}
