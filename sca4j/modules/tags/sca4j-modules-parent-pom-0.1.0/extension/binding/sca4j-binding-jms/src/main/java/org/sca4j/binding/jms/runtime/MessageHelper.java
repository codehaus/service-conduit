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
