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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.sca4j.idl.wsdl.version.WsdlVersionChecker.WsdlVersion;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;

import org.w3c.dom.Element;

/**
 * WSDL 1.1 processor implementation.
 * 
 * @version $Revsion$ $Date: 2008-02-06 18:08:27 +0000 (Wed, 06 Feb 2008) $
 */
public class Wsdl11Processor extends AbstractWsdlProcessor implements WsdlProcessor {
    
    /**
     * @param wsdlProcessorRegistry Injected default processor.
     */
    public Wsdl11Processor(WsdlProcessorRegistry wsdlProcessorRegistry) {
        wsdlProcessorRegistry.registerProcessor(WsdlVersion.VERSION_1_1, this);
    }

    /**
     * @see @see org.sca4j.idl.wsdl.processor.WsdlProcessor#getOperations(javax.xml.namespace.QName, java.net.URL)
     */
    public List<Operation<XmlSchemaType>> getOperations(QName portTypeOrInterfaceName, URL wsdlUrl) {
        
        try {
            
            List<Operation<XmlSchemaType>> operations = new LinkedList<Operation<XmlSchemaType>>();
            
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setExtensionRegistry(factory.newPopulatedExtensionRegistry());
            
            Definition definition = reader.readWSDL(wsdlUrl.toExternalForm());            
            PortType portType = definition.getPortType(portTypeOrInterfaceName);
            
            if(portType == null) {
                throw new WsdlProcessorException("Port type not found " + portTypeOrInterfaceName);
            }
            
            XmlSchemaCollection xmlSchema = getXmlSchema(definition);
            
            for(Object obj : portType.getOperations()) {                
                Operation<XmlSchemaType> op = getOperation(xmlSchema, obj);                
                operations.add(op);                
            }
            
            return operations;
            
        } catch (WSDLException ex) {
            throw new WsdlProcessorException("Unable to parse WSDL " + wsdlUrl, ex);
        }
        
    }

    /*
     * Creates an F3 operation from a WSDL operation.
     */
    private Operation<XmlSchemaType> getOperation(XmlSchemaCollection xmlSchema, Object obj) {
        
        javax.wsdl.Operation operation = (javax.wsdl.Operation) obj;
        
        Input input = operation.getInput();
        Output output = operation.getOutput();
        Map faults = operation.getFaults();
        
        String name = operation.getName();
        DataType<List<DataType<XmlSchemaType>>> inputType = getInputType(input, xmlSchema);
        DataType<XmlSchemaType> outputType = getOutputType(output, xmlSchema);
        List<DataType<XmlSchemaType>> faultTypes = getFaultTypes(faults, xmlSchema);
        
        return new Operation<XmlSchemaType>(name, inputType, outputType, faultTypes);
        
    }
    
    /*
     * Gets the fault types.
     */
    @SuppressWarnings("unchecked")
    private List<DataType<XmlSchemaType>> getFaultTypes(Map faults, XmlSchemaCollection xmlSchema) {
        
        List<DataType<XmlSchemaType>> types = new LinkedList<DataType<XmlSchemaType>>();
        
        for(Fault fault : (Collection<Fault>) faults.values()) {
            
            Part part = (Part) fault.getMessage().getOrderedParts(null).get(0);  
            DataType<XmlSchemaType> dataType = getDataType(part.getElementName(), xmlSchema);
            if(dataType != null) {
                types.add(dataType);
            }        
            
        }
        
        return types;
        
    }

    /*
     * Get the output type.
     */
    private DataType<XmlSchemaType> getOutputType(Output output, XmlSchemaCollection xmlSchema) {
        
        if(output == null) return null;
        
        Message message = output.getMessage();
        Part part = (Part) message.getOrderedParts(null).get(0);
        
        return getDataType(part.getElementName(), xmlSchema);
        
    }

    /*
     * Get the input type.
     */
    @SuppressWarnings("unchecked")
    private DataType<List<DataType<XmlSchemaType>>> getInputType(Input input, XmlSchemaCollection xmlSchema) {
        
        if(input == null) return null;
        
        Message message = input.getMessage();
        List<Part> parts = message.getOrderedParts(null);
        
        List<DataType<XmlSchemaType>> types = new LinkedList<DataType<XmlSchemaType>>();
        
        for(Part part : parts) {    
            DataType<XmlSchemaType> dataType = getDataType(part.getElementName(), xmlSchema);
            if(dataType != null) {
                types.add(dataType);
            }        
        }
        
        return new DataType<List<DataType<XmlSchemaType>>>(Object.class, types);
        
    }

    /*
     * Get all the inline schemas.
     */
    private XmlSchemaCollection getXmlSchema(Definition definition) {
        
        XmlSchemaCollection collection = new XmlSchemaCollection();
        Types types = definition.getTypes();
        for(Object obj : types.getExtensibilityElements()) {
            if(obj instanceof Schema) {
                Schema schema = (Schema) obj;
                Element element = schema.getElement();
                collection.setBaseUri(schema.getDocumentBaseURI());
                collection.read(element);
            }
        }
        return collection;
        
    }

}
