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
package org.sca4j.bpel.lightweight.introspection;

import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.bpel.introspection.Constants;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.bpel.lightweight.model.ImportDefinition;
import org.sca4j.bpel.lightweight.model.PartnerLinkDefinition;
import org.sca4j.bpel.lightweight.model.SequenceDefinition;
import org.sca4j.bpel.lightweight.model.VariableDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;

public class BpelProcessLoader implements TypeLoader<BpelProcessDefinition> {
    
    private ImportLoader importLoader = new ImportLoader();
    private VariableLoader variableLoader = new VariableLoader();
    private PartnerLinkLoader partnerLinkLoader = new PartnerLinkLoader();
    private SequenceLoader sequenceLoader = new SequenceLoader();

    @Override
    public BpelProcessDefinition load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        
        String name = reader.getAttributeValue(null, "name");
        String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
        QName processName = new QName(targetNamespace, name);
        BpelProcessDefinition bpelProcessDefinition = new BpelProcessDefinition(processName);

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName elementName = reader.getName();
                    if (elementName.equals(Constants.IMPORT_ELEMENT)) {
                        ImportDefinition importDefinition = importLoader.load(reader, context);
                        bpelProcessDefinition.getImports().add(importDefinition);
                    } else if (elementName.equals(Constants.VARIABLE_ELEMENT)) {
                        VariableDefinition variableDefinition = variableLoader.load(reader, context);
                        bpelProcessDefinition.getVariables().add(variableDefinition);
                    } else if (elementName.equals(Constants.PARTNERLINK_ELEMENT)) {
                        PartnerLinkDefinition partnerLinkDefinition = partnerLinkLoader.load(reader, context);
                        bpelProcessDefinition.getPartnerLinks().add(partnerLinkDefinition);
                    } else if (elementName.equals(Constants.SEQUENCE_ELEMENT)) {
                        SequenceDefinition sequenceDefinition = sequenceLoader.load(reader, context);
                        bpelProcessDefinition.getSequences().add(sequenceDefinition);
                    }
                    break;
                case END_DOCUMENT:
                    return bpelProcessDefinition;
            }
        }
        
    }

}
