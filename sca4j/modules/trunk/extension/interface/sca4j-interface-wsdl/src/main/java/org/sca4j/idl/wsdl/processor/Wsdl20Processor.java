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

package org.sca4j.idl.wsdl.processor;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.schema.Schema;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.ElementDeclaration;
import org.apache.woden.wsdl20.Interface;
import org.apache.woden.wsdl20.InterfaceFaultReference;
import org.apache.woden.wsdl20.InterfaceMessageReference;
import org.apache.woden.wsdl20.InterfaceOperation;
import org.apache.woden.wsdl20.enumeration.Direction;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.sca4j.idl.wsdl.version.WsdlVersionChecker.WsdlVersion;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.w3c.dom.Document;

/**
 * WSDL 2.0 processor implementation.
 * 
 * @version $Revsion$ $Date: 2008-02-06 18:08:27 +0000 (Wed, 06 Feb 2008) $
 */
public class Wsdl20Processor extends AbstractWsdlProcessor implements WsdlProcessor {
    
    /**
     * @param wsdlProcessorRegistry Injected default processor.
     */
    public Wsdl20Processor(WsdlProcessorRegistry wsdlProcessorRegistry) {
        wsdlProcessorRegistry.registerProcessor(WsdlVersion.VERSION_2_0, this);
    }

    /**
     * @see @see org.sca4j.idl.wsdl.processor.WsdlProcessor#getOperations(javax.xml.namespace.QName, java.net.URL)
     */
    public List<Operation<XmlSchemaType>> getOperations(QName portTypeOrInterfaceName, URL wsdlUrl) {
        
        try {
            
            List<Operation<XmlSchemaType>> operations = new LinkedList<Operation<XmlSchemaType>>();
            
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            
            Description description = reader.readWSDL(wsdlUrl.toExternalForm());
            Interface interfaze = description.getInterface(portTypeOrInterfaceName);
            
            if(interfaze == null) {
                throw new WsdlProcessorException("Interface not found " + portTypeOrInterfaceName);
            }
            
            XmlSchemaCollection xmlSchema = getXmlSchema(description);
            
            for(InterfaceOperation operation : interfaze.getAllInterfaceOperations()) {
                Operation<XmlSchemaType> op = getOperation(xmlSchema, operation);                
                operations.add(op);                
            }
            
            return operations;
            
        } catch (WSDLException ex) {
            throw new WsdlProcessorException("Unable to parse WSDL " + wsdlUrl, ex);
        }
        
    }

    /*
     * Creates F3 operation from WSDL 2.0 operation.
     */
    private Operation<XmlSchemaType> getOperation(XmlSchemaCollection xmlSchema, InterfaceOperation operation) {
        
        String name = operation.getName().getLocalPart();
        
        InterfaceMessageReference[] messageReferences = operation.getInterfaceMessageReferences();
        
        List<DataType<XmlSchemaType>> faultTypes = getFaultTypes(xmlSchema, operation);
        
        List<DataType<XmlSchemaType>> inputTypes = new LinkedList<DataType<XmlSchemaType>>();
        DataType<XmlSchemaType> outputType = null;
        
        for(InterfaceMessageReference messageReference : messageReferences) {
            ElementDeclaration elementDeclaration = messageReference.getElementDeclaration();
            QName qName = elementDeclaration.getName();
            DataType<XmlSchemaType> dataType = getDataType(qName, xmlSchema);
            if(dataType != null) {
                if(messageReference.getDirection().equals(Direction.IN)) {
                    inputTypes.add(dataType);
                } else if(messageReference.getDirection().equals(Direction.OUT)) {
                    if(outputType != null) {
                        throw new WsdlProcessorException("Multipart output is not supported");
                    }
                    outputType = dataType;
                }
                // TODO What about in out?
            }
        }
        
        DataType<List<DataType<XmlSchemaType>>> inputType = new DataType<List<DataType<XmlSchemaType>>>(Object.class, inputTypes);
        
        return new Operation<XmlSchemaType>(name, inputType, outputType, faultTypes);
    }

    /*
     * Gets the fault types.
     */
    private List<DataType<XmlSchemaType>> getFaultTypes(XmlSchemaCollection xmlSchema, InterfaceOperation operation) {
        
        InterfaceFaultReference[] faultReferences = operation.getInterfaceFaultReferences();
        List<DataType<XmlSchemaType>> faultTypes = new LinkedList<DataType<XmlSchemaType>>();
        for(InterfaceFaultReference faultReference : faultReferences) {
            ElementDeclaration elementDeclaration = faultReference.getInterfaceFault().getElementDeclaration();
            QName qName = elementDeclaration.getName();
            DataType<XmlSchemaType> dataType = getDataType(qName, xmlSchema);
            if(dataType != null) {
                faultTypes.add(dataType);
            }
        }
        return faultTypes;
        
    }

    /*
     * Get all the inline schemas.
     */
    private XmlSchemaCollection getXmlSchema(Description description) {
        
        XmlSchemaCollection collection = new XmlSchemaCollection();
        
        Schema[] schemas = description.toElement().getTypesElement().getSchemas();
        for(Schema schema : schemas) {
            for(Document doc : schema.getSchemaDefinition().getAllSchemas()) {
                collection.read(doc.getDocumentElement());
            }
        }
        
        return collection;
        
    }

}
