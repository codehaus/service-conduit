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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.bpel.introspection.Constants;
import org.sca4j.bpel.lightweight.model.AssignDefinition;
import org.sca4j.bpel.lightweight.model.InvokeDefinition;
import org.sca4j.bpel.lightweight.model.ReceiveDefinition;
import org.sca4j.bpel.lightweight.model.ReplyDefinition;
import org.sca4j.bpel.lightweight.model.SequenceDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;

public class SequenceLoader implements TypeLoader<SequenceDefinition> {
    
    private AssignLoader assignLoader = new AssignLoader();
    private InvokeLoader invokeLoader = new InvokeLoader();
    private ReplyLoader replyLoader = new ReplyLoader();
    private ReceiveLoader receiveLoader = new ReceiveLoader();

    @Override
    public SequenceDefinition load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        
        SequenceDefinition sequenceDefinition = new SequenceDefinition();

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName elementName = reader.getName();
                    if (elementName.equals(Constants.ASSIGN_ELEMENT)) {
                        AssignDefinition assignDefinition = assignLoader.load(reader, context);
                        sequenceDefinition.getActivities().add(assignDefinition);
                    } else if (elementName.equals(Constants.INVOKE_ELEMENT)) {
                        InvokeDefinition invokeDefinition = invokeLoader.load(reader, context);
                        sequenceDefinition.getActivities().add(invokeDefinition);
                    } else if (elementName.equals(Constants.REPLY_ELEMENT)) {
                        ReplyDefinition replyDefinition = replyLoader.load(reader, context);
                        sequenceDefinition.getActivities().add(replyDefinition);
                    } else if (elementName.equals(Constants.RECEIVE_ELEMENT)) {
                        ReceiveDefinition receiveDefinition = receiveLoader.load(reader, context);
                        sequenceDefinition.getActivities().add(receiveDefinition);
                    } else {
                        throw new XMLStreamException("Unexpected element within sequence " + elementName);
                    }
                    break;
                case END_ELEMENT:
                    elementName = reader.getName();
                    if (elementName.equals(Constants.SEQUENCE_ELEMENT)) {
                        return sequenceDefinition;
                    }
            }
        }
        
    }

}
