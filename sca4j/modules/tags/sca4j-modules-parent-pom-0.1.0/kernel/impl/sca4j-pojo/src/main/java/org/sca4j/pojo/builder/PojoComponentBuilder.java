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
package org.sca4j.pojo.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.expression.ExpressionExpander;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformerRegistry;

/**
 * Base class for ComponentBuilders that build components based on POJOs.
 *
 * @version $Rev: 5324 $ $Date: 2008-09-04 04:33:34 +0100 (Thu, 04 Sep 2008) $
 */
public abstract class PojoComponentBuilder<T, PCD extends PojoComponentDefinition, C extends Component> implements ComponentBuilder<PCD, C> {

    protected final ComponentBuilderRegistry builderRegistry;
    protected final ScopeRegistry scopeRegistry;
    protected final InstanceFactoryBuilderRegistry providerBuilders;
    protected final ClassLoaderRegistry classLoaderRegistry;
    protected final TransformerRegistry<PullTransformer<?, ?>> transformerRegistry;
    protected ExpressionExpander expander;

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
                                   ClassLoaderRegistry classLoaderRegistry,
                                   TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        this.builderRegistry = builderRegistry;
        this.scopeRegistry = scopeRegistry;
        this.providerBuilders = providerBuilders;
        this.classLoaderRegistry = classLoaderRegistry;
        this.transformerRegistry = transformerRegistry;
    }

    /**
     * Optional ExpressionExpander for substituting values for properties containing expressions of the form '${..}'. Values may be sourced from a
     * variety of places, including a file or system property.
     *
     * @param expander the injected expander
     */
    @Reference(required = false)
    public void setExpander(ExpressionExpander expander) {
        this.expander = expander;
    }

    protected void createPropertyFactories(PCD definition, InstanceFactoryProvider<T> provider) throws BuilderException {
        Map<String, Document> propertyValues = definition.getPropertyValues();

        ClassLoader cl = classLoaderRegistry.getClassLoader(definition.getClassLoaderId());
        TransformContext context = new TransformContext(null, cl, null, null);
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

        DataType<?> targetType = null;

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
            if (instance instanceof String && expander != null) {
                // if the property value is a string, expand it if it contains expressions
                instance = expander.expand((String) instance);
            }
            return new SingletonObjectFactory(instance);
        } catch (Exception e) {
            throw new PropertyTransformException("Unable to transform property value: " + name, name, e);
        }

    }

}
