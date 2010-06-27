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
package org.sca4j.binding.jms.runtime.wireformat;

import java.io.StringReader;
import java.io.StringWriter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.sca4j.binding.jms.common.SCA4JJmsException;

public class DataBinder {
    
    public Object unmarshal(Message jmsMessage, Class<?> type) {
        
        try {
            if (String.class.equals(type)) {
                return ((TextMessage) jmsMessage).getText();
            } else if (Message.class.isAssignableFrom(type)) {
                return jmsMessage;
            } else if (type.getAnnotation(XmlRootElement.class) != null) {
                String text = ((TextMessage) jmsMessage).getText();
                return JAXBContext.newInstance(type).createUnmarshaller().unmarshal(new StringReader(text)); // TODO MKU Optimize
            } else if (Object.class.equals(type)) {
                return ((TextMessage) jmsMessage).getText();
            }
            throw new IllegalArgumentException("Type not supported " + type);
        } catch (JMSException e) {
            throw new SCA4JJmsException("Unable to unmarshal type " + type, e);
        } catch (JAXBException e) {
            throw new SCA4JJmsException("Unable to unmarshal type " + type, e);
        }
        
    }
    
    public Message marshal(Object value, Class<?> type, Session session) {
        
        try {
            if (String.class.equals(type)) {
                return session.createTextMessage(value.toString());
            } else if (Message.class.isAssignableFrom(type)) {
                return (Message) value;
            } else if (type.getAnnotation(XmlRootElement.class) != null) {
                StringWriter stringWriter = new StringWriter();
                JAXBContext.newInstance(type).createMarshaller().marshal(value, stringWriter); // TODO MKU Optimize
                return session.createTextMessage(stringWriter.toString());
            } else if (Object.class.equals(type)) {
                return session.createTextMessage(value.toString());
            }
            throw new IllegalArgumentException("Type not supported " + type);
        } catch (JMSException e) {
            throw new SCA4JJmsException("Unable to unmarshal type " + type, e);
        } catch (JAXBException e) {
            throw new SCA4JJmsException("Unable to unmarshal type " + type, e);
        }
        
    }

}
