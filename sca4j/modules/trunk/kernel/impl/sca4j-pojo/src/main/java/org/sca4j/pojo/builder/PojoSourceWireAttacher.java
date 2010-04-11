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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.pojo.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.sca4j.pojo.component.PojoComponent;
import org.sca4j.pojo.provision.PojoWireSourceDefinition;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.spi.model.type.JavaParameterizedType;
import org.sca4j.spi.model.type.XSDSimpleType;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.TransformerRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @version $Revision$ $Date$
 */
public abstract class PojoSourceWireAttacher {

    private static final XSDSimpleType SOURCE_TYPE = new XSDSimpleType(Node.class, XSDSimpleType.STRING);

    protected TransformerRegistry<PullTransformer<?, ?>> transformerRegistry;

    protected PojoSourceWireAttacher(TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        this.transformerRegistry = transformerRegistry;
    }

    @SuppressWarnings("unchecked")
    protected Object getKey(PojoWireSourceDefinition sourceDefinition,
                            PojoComponent<?> source,
                            PhysicalWireTargetDefinition targetDefinition,
                            InjectableAttribute referenceSource) throws PropertyTransformException {

        if (!Map.class.isAssignableFrom(source.getMemberType(referenceSource))) {
            return null;
        }

        Document keyDocument = sourceDefinition.getKey();
        if (keyDocument == null) {
        	keyDocument = targetDefinition.getKey();
        }


        if (keyDocument != null) {

            Element element = keyDocument.getDocumentElement();

            Type formalType;
            Type type = source.getGerenricMemberType(referenceSource);

            if (type instanceof ParameterizedType) {
                ParameterizedType genericType = (ParameterizedType) type;
                formalType = genericType.getActualTypeArguments()[0];
                if (formalType instanceof ParameterizedType && ((ParameterizedType) formalType).getRawType().equals(Class.class)) {
                    formalType = ((ParameterizedType) formalType).getRawType();
                } else if (formalType instanceof Class<?> && Enum.class.isAssignableFrom((Class<?>) formalType)) {
                    Class<Enum> enumClass = (Class<Enum>) formalType;
                    return Enum.valueOf(enumClass, element.getTextContent());
                }
            } else {
                formalType = String.class;
            }

            TransformContext context = new TransformContext();
            return createKey(formalType, element, context);
        }

        return null;

    }

    @SuppressWarnings("unchecked")
    private Object createKey(Type type, Element value, TransformContext context) throws PropertyTransformException {

        DataType<?> targetType;
        if (type instanceof Class<?>) {
            targetType = new JavaClass((Class<?>) type);
        } else {
            targetType = new JavaParameterizedType((ParameterizedType) type);
        }
        PullTransformer<Node, ?> transformer = (PullTransformer<Node, ?>) transformerRegistry.getTransformer(SOURCE_TYPE, targetType);
        if (transformer == null) {
            throw new PropertyTransformException("No transformer for : " + type);
        }
        try {
            return transformer.transform(value, context);
        } catch (TransformationException e) {
            throw new PropertyTransformException("Error transformatng property", e);
        }
    }

}
