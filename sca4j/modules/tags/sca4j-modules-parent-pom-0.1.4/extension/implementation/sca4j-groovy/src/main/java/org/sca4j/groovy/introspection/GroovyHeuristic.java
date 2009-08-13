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
package org.sca4j.groovy.introspection;

import org.sca4j.groovy.scdl.GroovyImplementation;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Signature;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class GroovyHeuristic implements HeuristicProcessor<GroovyImplementation> {

    public void applyHeuristics(GroovyImplementation implementation, Class<?> implClass, IntrospectionContext context) {

        PojoComponentType componentType = implementation.getComponentType();

        if (componentType.getConstructor() == null) {
            try {
                componentType.setConstructor(new Signature(implClass.getConstructor()));
            } catch (NoSuchMethodException e) {
                throw new AssertionError();
            }
        }
    }
}
