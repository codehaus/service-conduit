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
package org.sca4j.idl.wsdl.model;

import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.sca4j.scdl.Operation;
import org.sca4j.spi.services.contribution.ResourceElement;

public class PortTypeResourceElement extends ResourceElement<QName> {
    
    private XmlSchemaCollection schemaCollection;
    private List<Operation> operations;
    private PortType portType;
    private Definition definition;
    
    public PortTypeResourceElement(QName symbol) {
        super(symbol);
    }

    public XmlSchemaCollection getSchemaCollection() {
        return schemaCollection;
    }

    public List<Operation> getOperations() {
        return operations;
    }
    
    public PortType getPortType() {
        return portType;
    }
    
    public Definition getDefinition() {
        return definition;
    }

    public void setSchemaCollection(XmlSchemaCollection schemaCollection) {
        this.schemaCollection = schemaCollection;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void setPortType(PortType portType, Definition definition) {
        this.portType = portType;
        this.definition = definition;
    }

}
