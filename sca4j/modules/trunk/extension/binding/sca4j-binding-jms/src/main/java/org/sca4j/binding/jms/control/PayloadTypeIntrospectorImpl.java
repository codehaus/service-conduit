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
package org.sca4j.binding.jms.control;

import static org.sca4j.host.Namespaces.SCA4J_NS;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import javax.xml.namespace.QName;

import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;

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
