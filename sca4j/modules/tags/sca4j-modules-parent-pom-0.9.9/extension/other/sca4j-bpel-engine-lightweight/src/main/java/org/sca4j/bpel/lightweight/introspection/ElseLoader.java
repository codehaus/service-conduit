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

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.bpel.introspection.Constants;
import org.sca4j.bpel.lightweight.model.AbstractActivity;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;

public class ElseLoader implements TypeLoader<List<AbstractActivity>> {
    
    private InvokeLoader invokeLoader = new InvokeLoader();
    private AssignLoader assignLoader = new AssignLoader();
    private ReplyLoader replyLoader = new ReplyLoader();

    @Override
    public List<AbstractActivity> load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        
        List<AbstractActivity> abstractActivities = new LinkedList<AbstractActivity>();
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName elementName = reader.getName();
                    if (elementName.equals(Constants.INVOKE_ELEMENT)) {
                        AbstractActivity abstractActivity = invokeLoader.load(reader, context);
                        abstractActivities.add(abstractActivity);
                    } else if (elementName.equals(Constants.ASSIGN_ELEMENT)) {
                        AbstractActivity abstractActivity = assignLoader.load(reader, context);
                        abstractActivities.add(abstractActivity);
                    } else if (elementName.equals(Constants.REPLY_ELEMENT)) {
                        AbstractActivity abstractActivity = replyLoader.load(reader, context);
                        abstractActivities.add(abstractActivity);
                    } else {
                        throw new XMLStreamException("Unexpected element within assign " + elementName);
                    }
                    break;
                case END_ELEMENT:
                    elementName = reader.getName();
                    if (elementName.equals(Constants.ELSE_ELEMENT)) {
                        return abstractActivities;
                    }
            }
            
        }
        
    }

}
