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
package org.sca4j.pojo.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.sca4j.pojo.instancefactory.InstanceFactoryBuilderRegistry;
import org.sca4j.pojo.provision.PojoComponentDefinition;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.SingletonObjectFactory;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilder;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.component.Component;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.spi.model.type.JavaParameterizedType;
import org.sca4j.spi.model.type.XSDSimpleType;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformerRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base class for ComponentBuilders that build components based on POJOs.
 *
 * @version $Rev: 5324 $ $Date: 2008-09-04 04:33:34 +0100 (Thu, 04 Sep 2008) $
 */
public abstract class PojoComponentBuilder<T, PCD extends PojoComponentDefinition, C extends Component> implements ComponentBuilder<PCD, C> {

    protected final ComponentBuilderRegistry builderRegistry;
    protected final ScopeRegistry scopeRegistry;
    protected final InstanceFactoryBuilderRegistry providerBuilders;
    protected final TransformerRegistry<PullTransformer<?, ?>> transformerRegistry;

    private static final XSDSimpleType SOURCE_TYPE = new XSDSimpleType(Node.class, XSDSimpleType.STRING);
    private static final Map<Type, Class<?>> OBJECT_TYPES;

    static {
        OBJECT_TYPES = new HashMap<Type, Class<?>>();
        OBJECT_TYPES.put(Boolean.TYPE, Boolean.class);
        OBJECT_TYPES.put(Byte.TYPE, Byte.class);
        OBJECT_TYPES.put(Short.TYPE, Short.class);
        OBJECT_TYPES.put(Integer.TYPE, Integer.class);
        OBJECT_TYPES.put(Long.TYPE, Long.class);
        OBJECT_TYPES.put(Float.TYPE, Float.class);
        OBJECT_TYPES.put(Double.TYPE, Double.class);
    }

    protected PojoComponentBuilder(ComponentBuilderRegistry builderRegistry,
                                   ScopeRegistry scopeRegistry,
                                   InstanceFactoryBuilderRegistry providerBuilders,
                                   TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        this.builderRegistry = builderRegistry;
        this.scopeRegistry = scopeRegistry;
        this.providerBuilders = providerBuilders;
        this.transformerRegistry = transformerRegistry;
    }

    protected void createPropertyFactories(PCD definition, InstanceFactoryProvider<T> provider) throws BuilderException {
        
        Map<String, Document> propertyValues = definition.getPropertyValues();
        TransformContext context = new TransformContext();
        for (Map.Entry<String, Document> entry : propertyValues.entrySet()) {
            String name = entry.getKey();
            Document value = entry.getValue();
            Element element = value.getDocumentElement();
            InjectableAttribute source = new InjectableAttribute(InjectableAttributeType.PROPERTY, name);

            Type memberType = provider.getGenericType(source);
            if (memberType instanceof Class<?> ||
                    (memberType instanceof ParameterizedType && ((ParameterizedType) memberType).getRawType().equals(Class.class))) {
                memberType = provider.getMemberType(source);
                if (((Class<?>) memberType).isPrimitive()) {
                    memberType = OBJECT_TYPES.get(memberType);
                }
            }

            ObjectFactory<?> objectFactory = createObjectFactory(name, memberType, element, context);
            provider.setObjectFactory(source, objectFactory);
        }
    }

    @SuppressWarnings("unchecked")
    private ObjectFactory<?> createObjectFactory(String name, Type type, Element value, TransformContext context) throws BuilderException {

        try {
            DataType<?> targetType = null;
            
            if (type instanceof Class<?>) {
                Class<?> clazz = (Class<?>) type;
                if (clazz.isAnnotationPresent(XmlRootElement.class)) {
                    Node jaxbNode = value.getFirstChild();
                    while (jaxbNode.getNodeType() != Node.ELEMENT_NODE) {
                        jaxbNode = jaxbNode.getNextSibling();
                    }
                    Object instance = JAXBContext.newInstance(clazz).createUnmarshaller().unmarshal(jaxbNode);
                    return new SingletonObjectFactory(instance);
                }
            }
    
            if (type instanceof Class<?>) {
                targetType = new JavaClass((Class<?>) type);
            } else if (type instanceof ParameterizedType) {
                targetType = new JavaParameterizedType((ParameterizedType) type);
            }
    
            PullTransformer<Node, ?> transformer = (PullTransformer<Node, ?>) transformerRegistry.getTransformer(SOURCE_TYPE, targetType);
            if (transformer == null) {
                throw new PropertyTransformException("No transformer for property of type: " + type, name, null);
            }
    
            try {
                Object instance = transformer.transform(value, context);
                return new SingletonObjectFactory(instance);
            } catch (Exception e) {
                throw new PropertyTransformException("Unable to transform property value: " + name, name, e);
            }
        } catch (JAXBException e) {
            throw new BuilderException(e);
        }

    }

}
