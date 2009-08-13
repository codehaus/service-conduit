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
