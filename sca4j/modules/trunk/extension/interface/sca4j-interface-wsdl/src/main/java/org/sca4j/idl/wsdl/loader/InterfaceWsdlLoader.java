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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.Constants;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.idl.wsdl.WsdlContract;
import org.sca4j.idl.wsdl.processor.PortTypeResourceElement;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;

/**
 * Loader for interface.wsdl.
 *
 * @version $Revision: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
@EagerInit
public class InterfaceWsdlLoader implements TypeLoader<WsdlContract>, Constants {

    private static final QName QNAME = new QName(SCA_NS, "interface.wsdl");

    @Reference public LoaderRegistry registry;
    @Reference public MetaDataStore metaDataStore;

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
        String interfaze = reader.getAttributeValue(null, "interface");
        LoaderUtil.skipToEndElement(reader);
        
        if (interfaze == null) {
            MissingAttribute failure = new MissingAttribute("Interface attribute is required", "interface", reader);
            context.addError(failure);
            return wsdlContract;
        }
        
        QName interfaceQName = parseInterface(interfaze, context, reader);
        if (interfaceQName == null) {
            return null;
        }
        wsdlContract.setPortTypeName(interfaceQName);
        
        try {
            PortTypeResourceElement resourceElement = metaDataStore.resolve(context.getContributionUri(), PortTypeResourceElement.class, interfaceQName, new DefaultValidationContext());
            wsdlContract.setOperations(resourceElement.getOperations());
        } catch (MetaDataStoreException e) {
            InvalidWsdlElement failure = new InvalidWsdlElement(e.getMessage(), interfaze, reader);
            context.addError(failure);
            return wsdlContract;
        }
        
        return wsdlContract;

    }

    private static QName parseInterface(String interfaze, IntrospectionContext context, XMLStreamReader reader) {
        
        String wsdlElementType = "wsdl.porttype(";
        
        String[] parts = interfaze.split("#");
        if (parts == null || parts.length != 2) {
            context.addError(new InvalidWsdlElement("Incorrect interface format", interfaze, reader));
            return null;
        }
        
        String targetNamespace = parts[0];
        if (!parts[1].startsWith(wsdlElementType) || !parts[1].endsWith(")")) {
            context.addError(new InvalidWsdlElement("Incorrect interface format", interfaze, reader));
            return null;
        }
        String localPart = parts[1].substring(wsdlElementType.length(), parts[1].lastIndexOf(')'));
        return new QName(targetNamespace, localPart);
    }
    
}
