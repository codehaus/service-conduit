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
package org.sca4j.pojo.control;

import java.lang.reflect.Method;

import org.sca4j.pojo.provision.PojoComponentDefinition;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Signature;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * @version $Rev: 5246 $ $Date: 2008-08-20 22:30:18 +0100 (Wed, 20 Aug 2008) $
 */
public interface InstanceFactoryGenerationHelper {
    Integer getInitLevel(ComponentDefinition<?> definition, PojoComponentType type);

    Signature getSignature(Method method);

    void processInjectionSites(LogicalComponent<? extends Implementation<PojoComponentType>> component, InstanceFactoryDefinition providerDefinition);

    /**
     * Set the actual values of the physical properties.
     *
     * @param component the component corresponding to the implementation
     * @param physical  the physical component whose properties should be set
     */
    void processPropertyValues(LogicalComponent<?> component, PojoComponentDefinition physical);
}
