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
package org.sca4j.system.introspection;

import java.lang.reflect.Constructor;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Signature;
import org.sca4j.scdl.validation.AmbiguousConstructor;
import org.sca4j.introspection.java.NoConstructorFound;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * Heuristic that selects the constructor to use.
 *
 * @version $Rev: 4360 $ $Date: 2008-05-26 10:33:45 +0100 (Mon, 26 May 2008) $
 */
public class SystemConstructorHeuristic implements HeuristicProcessor<SystemImplementation> {

    public void applyHeuristics(SystemImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();

        // if there is already a defined constructor then do nothing
        if (componentType.getConstructor() != null) {
            return;
        }

        Signature signature = findConstructor(implClass, context);
        componentType.setConstructor(signature);
    }

    /**
     * Find the constructor to use.
     * <p/>
     * For now, we require that the class have a single constructor or one annotated with @Constructor. If there is more than one, the default
     * constructor will be selected or an org.osoa.sca.annotations.Constructor annotation must be used.
     *
     * @param implClass the class we are inspecting
     * @param context   the introspection context to report errors and warnings
     * @return the signature of the constructor to use
     */
    Signature findConstructor(Class<?> implClass, IntrospectionContext context) {
        Constructor<?>[] constructors = implClass.getDeclaredConstructors();
        Constructor<?> selected = null;
        if (constructors.length == 1) {
            selected = constructors[0];
        } else {
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(org.osoa.sca.annotations.Constructor.class)) {
                    if (selected != null) {
                        context.addError(new AmbiguousConstructor(implClass));
                        return null;
                    }
                    selected = constructor;
                }
            }
            if (selected == null) {
                try {
                    selected = implClass.getConstructor();
                } catch (NoSuchMethodException e) {
                    context.addError(new NoConstructorFound(implClass));
                    return null;
                }
            }
        }
        return new Signature(selected);
    }

}
