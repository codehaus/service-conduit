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
package org.sca4j.binding.jms.control;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import javax.xml.namespace.QName;

import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;

import static org.sca4j.host.Namespaces.SCA4J_NS;

/**
 * Default implementation of the PayloadTypeIntrospector. Message types are
 * determined as follows:
 * 
 * <pre>
 * &lt;ul&gt;
 * &lt;li&gt;If the operation has a JAXB databinding intent, a text type is returned
 * &lt;li&gt;If the parameters are Serializable, an object message is returned
 * &lt;li&gt;If the parameters are primitives, the specific primitive type is returned
 * &lt;li&gt;If the parameters are a stream, a stream message is returned
 * &lt;ul&gt;
 * </pre>
 * 
 * @version $Revision$ $Date$
 */
public class PayloadTypeIntrospectorImpl implements PayloadTypeIntrospector {
    private static final QName DATABINDING_INTENT = new QName(SCA4J_NS, "dataBinding.jaxb");

    public <T> PayloadType introspect(Operation<T> operation) throws JmsGenerationException {
        // TODO perform error checking, e.g. mixing of databindings
        if (operation.getIntents().contains(DATABINDING_INTENT)) {
            return PayloadType.TEXT;
        }
        DataType<List<DataType<T>>> inputType = operation.getInputType();
        if (inputType.getLogical().size() == 1) {
            DataType<?> param = inputType.getLogical().get(0);
            Type physical = param.getPhysical();
            if (physical instanceof Class) {
                Class<?> clazz = (Class<?>) physical;
                if (clazz.isPrimitive()) {
                    return calculatePrimitivePayloadType(clazz);
                } else if (InputStream.class.isAssignableFrom(clazz)) {
                    return PayloadType.STREAM;
                } else if (String.class.isAssignableFrom(clazz)) {
                    return PayloadType.TEXT;
                } else if (Serializable.class.isAssignableFrom(clazz)) {
                    return PayloadType.OBJECT;
                }
            } else {
                throw new UnsupportedOperationException("Non-class types not supported: " + physical);
            }
        }
        // more than one parameter, use an object type message
        return PayloadType.OBJECT;
    }

    private PayloadType calculatePrimitivePayloadType(Class<?> clazz) throws JmsGenerationException {
        if (Short.TYPE.equals(clazz)) {
            return PayloadType.SHORT;
        } else if (Integer.TYPE.equals(clazz)) {
            return PayloadType.INTEGER;
        } else if (Double.TYPE.equals(clazz)) {
            return PayloadType.DOUBLE;
        } else if (Float.TYPE.equals(clazz)) {
            return PayloadType.FLOAT;
        } else if (Long.TYPE.equals(clazz)) {
            return PayloadType.LONG;
        } else if (Character.TYPE.equals(clazz)) {
            return PayloadType.CHARACTER;
        } else if (Boolean.TYPE.equals(clazz)) {
            return PayloadType.BOOLEAN;
        } else if (Byte.TYPE.equals(clazz)) {
            return PayloadType.BYTE;
        } else {
            throw new JmsGenerationException("Parameter type not supported: " + clazz);
        }

    }

}
