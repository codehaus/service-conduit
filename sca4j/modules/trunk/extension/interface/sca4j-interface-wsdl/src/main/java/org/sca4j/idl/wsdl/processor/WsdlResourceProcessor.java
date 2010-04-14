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
 */
package org.sca4j.idl.wsdl.processor;

import java.net.URI;
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
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.model.type.XSDElement;
import org.sca4j.spi.model.type.XSDType;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceProcessor;
import org.w3c.dom.Element;

public class WsdlResourceProcessor implements ResourceProcessor {

    @Override
    public void index(Contribution contribution, URL url, ValidationContext context) throws ContributionException {
        Resource resource = new Resource(url);
        contribution.addResource(resource);
    }

    @Override
    public void process(URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader) throws ContributionException {
        
        try {
            
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setExtensionRegistry(factory.newPopulatedExtensionRegistry());
        
            Definition definition = reader.readWSDL(resource.getUrl().toExternalForm());
            XmlSchemaCollection schemaCollection = getXmlSchema(definition);
            
            for (Object object : definition.getPortTypes().keySet()) {
                
                QName portTypeName = (QName) object;
                PortType portType = (PortType) definition.getPortType(portTypeName);
                
                List<Operation> operations = new LinkedList<Operation>();
                for(Object obj : portType.getOperations()) {     
                    Operation op = getOperation(schemaCollection, (javax.wsdl.Operation) obj);                
                    operations.add(op);                
                }
                
                PortTypeResourceElement resorceElement = new PortTypeResourceElement(portTypeName, portType, schemaCollection, operations);
                resource.addResourceElement(resorceElement);
            }
            
        } catch (WSDLException e) {
            throw new ContributionException(e);
        }
    }

    /*
     * Creates an F3 operation from a WSDL operation.
     */
    private Operation getOperation(XmlSchemaCollection xmlSchema, javax.wsdl.Operation operation) {
        
        Input input = operation.getInput();
        Output output = operation.getOutput();
        Map<?, ?> faults = operation.getFaults();
        
        String name = operation.getName();
        List<DataType> inputType = getInputType(input, xmlSchema);
        DataType outputType = getOutputType(output, xmlSchema);
        List<DataType> faultTypes = getFaultTypes(faults, xmlSchema);
        
        return new Operation(name, inputType, outputType, faultTypes);
        
    }
    
    /*
     * Gets the fault types.
     */
    @SuppressWarnings("unchecked")
    private List<DataType> getFaultTypes(Map faults, XmlSchemaCollection xmlSchema) {
        
        List<DataType> types = new LinkedList<DataType>();
        
        for(Fault fault : (Collection<Fault>) faults.values()) {
            
            Part part = (Part) fault.getMessage().getOrderedParts(null).get(0);  
            DataType dataType = getDataType(part.getElementName(), xmlSchema);
            if(dataType != null) {
                types.add(dataType);
            }        
            
        }
        
        return types;
        
    }

    /*
     * Get the output type.
     */
    private DataType getOutputType(Output output, XmlSchemaCollection xmlSchema) {
        
        if(output == null) return null;
        
        Message message = output.getMessage();
        Part part = (Part) message.getOrderedParts(null).get(0);
        
        return getDataType(part.getElementName(), xmlSchema);
        
    }

    /*
     * Get the input type.
     */
    @SuppressWarnings("unchecked")
    private List<DataType> getInputType(Input input, XmlSchemaCollection xmlSchema) {
        
        if(input == null) return null;
        
        Message message = input.getMessage();
        List<Part> parts = message.getOrderedParts(null);
        
        List<DataType> types = new LinkedList<DataType>();
        
        for(Part part : parts) {    
            DataType dataType = getDataType(part.getElementName(), xmlSchema);
            if(dataType != null) {
                types.add(dataType);
            }        
        }
        
        return types;
        
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
    
    /*
     * Create a data type with the XML type for the part.
     */
    private DataType getDataType(QName qName, XmlSchemaCollection xmlSchema) {

        XmlSchemaType type = xmlSchema.getTypeByQName(qName);
        if(type != null) {
            return new XSDType(type.getQName());
        } else {
            XmlSchemaElement element = xmlSchema.getElementByQName(qName);
            if(element != null) {
                return new XSDElement(element.getQName());
            }
        }
        throw new WsdlProcessorException("Unable to find type " + qName);
        
    }

}
