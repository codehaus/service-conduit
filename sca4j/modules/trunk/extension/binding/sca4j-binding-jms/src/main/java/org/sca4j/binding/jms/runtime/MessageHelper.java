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
package org.sca4j.binding.jms.runtime;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.sca4j.binding.jms.provision.PayloadType;

/**
 * @version $Revision$ $Date$
 */
public class MessageHelper {

    public static Message createBytesMessage(Session session, Object payload, PayloadType payloadType) throws JMSException {
        BytesMessage message = session.createBytesMessage();
        switch (payloadType) {

        case BOOLEAN:
            message.writeBoolean((Boolean) payload);
            break;
        case BYTE:
            message.writeByte((Byte) payload);
            break;
        case CHARACTER:
            message.writeChar((Character) payload);
            break;
        case DOUBLE:
            message.writeDouble((Double) payload);
            break;
        case FLOAT:
            message.writeFloat((Float) payload);
            break;
        case INTEGER:
            message.writeInt((Integer) payload);
            break;
        case LONG:
            message.writeLong((Long) payload);
            break;
        case SHORT:
            message.writeShort((Short) payload);
            break;
        }
        return message;
    }

    public static Object getPayload(Message message, PayloadType payloadType) throws JMSException {
        Object payload;
        switch (payloadType) {
        case OBJECT:
            ObjectMessage objectMessage = (ObjectMessage) message;
            payload = objectMessage.getObject();
            break;
        case STREAM:
            throw new UnsupportedOperationException("Stream message not yet supported");
        case TEXT:
            TextMessage textMessage = (TextMessage) message;
            payload = textMessage.getText();
            break;
        case BOOLEAN:
            BytesMessage booleanMessage = (BytesMessage) message;
            return booleanMessage.readBoolean();
        case BYTE:
            BytesMessage bytesMessage = (BytesMessage) message;
            return bytesMessage.readByte();
        case CHARACTER:
            BytesMessage charMessage = (BytesMessage) message;
            return charMessage.readChar();
        case DOUBLE:
            BytesMessage doubleMessage = (BytesMessage) message;
            return doubleMessage.readDouble();
        case FLOAT:
            BytesMessage floatMessage = (BytesMessage) message;
            return floatMessage.readFloat();
        case INTEGER:
            BytesMessage intMessage = (BytesMessage) message;
            return intMessage.readInt();
        case LONG:
            BytesMessage longMessage = (BytesMessage) message;
            return longMessage.readLong();
        case SHORT:
            BytesMessage shortMessage = (BytesMessage) message;
            return shortMessage.readShort();
        default:
            throw new UnsupportedOperationException("Unsupported message type: " + message);
        }
        return payload;
    }

    private MessageHelper() {
    }
}
