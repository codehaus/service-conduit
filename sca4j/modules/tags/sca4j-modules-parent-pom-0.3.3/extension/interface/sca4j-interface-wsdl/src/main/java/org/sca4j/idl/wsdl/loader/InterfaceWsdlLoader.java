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

package org.sca4j.idl.wsdl.loader;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;

import org.sca4j.idl.wsdl.WsdlContract;
import org.sca4j.idl.wsdl.processor.WsdlProcessor;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * Loader for interface.wsdl.
 *
 * @version $Revision: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
@EagerInit
public class InterfaceWsdlLoader implements TypeLoader<WsdlContract>, Constants {

    /**
     * Interface element QName.
     */
    private static final QName QNAME = new QName(SCA_NS, "interface.wsdl");

    private LoaderRegistry registry;
    /**
     * WSDL processor.
     */
    private WsdlProcessor processor;

    /**
     * @param loaderRegistry Loader registry.
     * @param processor      WSDL processor.
     */
    protected InterfaceWsdlLoader(LoaderRegistry loaderRegistry, WsdlProcessor processor) {
        registry = loaderRegistry;
        this.processor = processor;
    }

    @Init
    public void start() {
        registry.registerLoader(QNAME, this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(QNAME);
    }

    public WsdlContract load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {

        WsdlContract wsdlContract = new WsdlContract();

        URL wsdlUrl = resolveWsdl(reader, context);
        if (wsdlUrl == null) {
            // there was a problem, return an empty contract
            return wsdlContract;
        }
        processInterface(reader, wsdlContract, wsdlUrl, context);

        processCallbackInterface(reader, wsdlContract, wsdlUrl);

        return wsdlContract;

    }

    /*
     * Processes the callback interface.
     */
    @SuppressWarnings("unchecked")
    private void processCallbackInterface(XMLStreamReader reader, WsdlContract wsdlContract, URL wsdlUrl) {

        String callbackInterfaze = reader.getAttributeValue(null, "callbackInterface");
        if (callbackInterfaze != null) {
            QName callbackInterfaceQName = getQName(callbackInterfaze);
            wsdlContract.setCallbackQname(callbackInterfaceQName);
        }

    }

    /*
     * Processes the interface.
     */
    @SuppressWarnings("unchecked")
    private void processInterface(XMLStreamReader reader, WsdlContract wsdlContract, URL wsdlUrl, IntrospectionContext context) {

        String interfaze = reader.getAttributeValue(null, "interface");
        if (interfaze == null) {
            MissingAttribute failure = new MissingAttribute("Interface attribute is required", "interface", reader);
            context.addError(failure);
            return;
        }
        QName interfaceQName = getQName(interfaze);
        wsdlContract.setQname(interfaceQName);
        wsdlContract.setOperations(processor.getOperations(interfaceQName, wsdlUrl));

    }

    /*
     * Resolves the WSDL.
     */
    private URL resolveWsdl(XMLStreamReader reader, IntrospectionContext context) {

        String wsdlLocation = reader.getAttributeValue(null, "wsdlLocation");
        if (wsdlLocation == null) {
            // We don't support auto dereferecing of namespace URI
            MissingAttribute failure = new MissingAttribute("wsdlLocation Location is required", "wsdlLocation", reader);
            context.addError(failure);
            return null;
        }
        URL wsdlUrl = getWsdlUrl(wsdlLocation);
        if (wsdlUrl == null) {
            InvalidWSDLLocation failure = new InvalidWSDLLocation("Unable to locate WSDL: " + wsdlLocation, wsdlLocation, reader);
            context.addError(failure);
        }
        return wsdlUrl;

    }

    /*
     * Returns the interface.portType qname.
     */
    private QName getQName(String interfaze) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    /*
    * Gets the WSDL URL.
    */
    private URL getWsdlUrl(String wsdlPath) {

        try {
            return new URL(wsdlPath);
        } catch (MalformedURLException ex) {
            return getClass().getClassLoader().getResource(wsdlPath);
        }
    }

}
