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
package org.sca4j.introspection;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;

/**
 * A mapping from formal types to actual types.
 *
 * @version $Rev: 3105 $ $Date: 2008-03-15 16:47:31 +0000 (Sat, 15 Mar 2008) $
 */
public class TypeMapping {
    private final Map<? super Type, Type> mappings = new HashMap<Type, Type>();

    /**
     * Add a mapping from a TypeVariable to an actual type
     *
     * @param typeVariable the formal type variable
     * @param type         the actual type it maps to
     */
    public void addMapping(TypeVariable<?> typeVariable, Type type) {
        mappings.put(typeVariable, type);
    }

    /**
     * Return the actual type of the supplied formal type.
     *
     * @param type the formal type parameter
     * @return the actual type; may be a TypeVariable if the type is not bound
     */
    public Type getActualType(Type type) {
        while (true) {
            Type actual = mappings.get(type);
            if (actual == null) {
                return type;
            } else {
                type = actual;
            }
        }
    }

    /**
     * Return the raw type of the supplied formal type.
     *
     * @param type the formal type parameter
     * @return the actual class for that parameter
     */
    public Class<?> getRawType(Type type) {
        Type actualType = getActualType(type);
        if (actualType instanceof Class<?>) {
            return (Class<?>) actualType;
        } else if (actualType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) actualType;
            return getRawType(typeVariable.getBounds()[0]);
        } else if (actualType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) actualType;
            return (Class<?>) parameterizedType.getRawType();
        } else if (actualType instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) actualType;
            Class<?> componentType = getRawType(arrayType.getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        } else if (actualType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) actualType;
            Type[] bounds = wildcardType.getUpperBounds();
            return getRawType(bounds[0]);
        } else {
            throw new AssertionError();
        }
    }
}
