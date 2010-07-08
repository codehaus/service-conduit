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
package org.sca4j.fabric.generator.wire;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;

/**
 * @version $Revision$ $Date$
 */
public class PhysicalOperationHelperImpl implements PhysicalOperationHelper {

    public PhysicalOperationDefinition mapOperation(Operation o) {

        if (o == null) {
            return null;
        }
        
        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        operation.setName(o.getName());
        operation.setEndsConversation(o.getConversationSequence() == Operation.CONVERSATION_END);
        Type returnType = o.getOutputType().getJavaType();
        operation.setReturnType(getClassName(returnType));

        List<? extends DataType> params = o.getInputTypes();
        for (DataType param : params) {
            Type paramType = param.getJavaType();
            operation.addParameter(getClassName(paramType));
        }
        return operation;

    }

    @SuppressWarnings({"unchecked"})
    public String getClassName(Type paramType) {
        
        if (paramType == null) {
            return null;
        }

        // TODO this needs to be fixed
        if (paramType instanceof Class) {
            return ((Class) paramType).getName();
        } else if (paramType instanceof ParameterizedType) {
            Type type = ((ParameterizedType) paramType).getRawType();
            if (type instanceof Class) {
                return ((Class) type).getName();
            }
        } else if (paramType instanceof TypeVariable) {
            TypeVariable var = (TypeVariable) paramType;
            if (var.getBounds().length > 0 && var.getBounds()[0] instanceof Class) {
                return ((Class) var.getBounds()[0]).getName();
            } else if (var.getBounds().length > 0 && var.getBounds()[0] instanceof ParameterizedType) {
                Type actualType = ((ParameterizedType) var.getBounds()[0]).getRawType();
                if (!(actualType instanceof Class)) {
                    throw new AssertionError();
                }
                return ((Class) actualType).getName();
            }
        } else if (paramType instanceof GenericArrayType) {
            GenericArrayType var = (GenericArrayType) paramType;
            return "[L" + var.getGenericComponentType();
        }
        throw new AssertionError();

    }

}
