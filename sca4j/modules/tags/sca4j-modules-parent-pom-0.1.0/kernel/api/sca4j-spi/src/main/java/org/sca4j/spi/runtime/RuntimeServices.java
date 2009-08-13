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
package org.sca4j.spi.runtime;

import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;

/**
 * Interface for accessing services provided by a runtime.
 * <p/>
 * These are the primoridal services that should be provided by all runtime implementations for use by other runtime components.
 *
 * @version $Rev: 5267 $ $Date: 2008-08-25 09:43:24 +0100 (Mon, 25 Aug 2008) $
 */
public interface RuntimeServices {

    /**
     * Returns this runtime's logical component manager.
     *
     * @return this runtime's logical component manager
     */
    LogicalComponentManager getLogicalComponentManager();

    /**
     * Returns this runtime's physical component manager.
     *
     * @return this runtime's physical component manager
     */
    ComponentManager getComponentManager();

    /**
     * Returns the ScopeRegistry used to manage runtime ScopeContainers.
     *
     * @return the ScopeRegistry used to manage runtime ScopeContainers
     */
    ScopeRegistry getScopeRegistry();

    /**
     * Returns the ScopeContainer used to manage runtime component instances.
     *
     * @return the ScopeContainer used to manage runtime component instances
     */
    ScopeContainer<?> getScopeContainer();

    /**
     * Returns the ClassLoaderRegistry used to manage runtime classloaders.
     *
     * @return the ClassLoaderRegistry used to manage runtime classloaders
     */
    ClassLoaderRegistry getClassLoaderRegistry();

    /**
     * Returns the MetaDataStore used to index contribution resources.
     *
     * @return the MetaDataStore used to index contribution resources
     */
    MetaDataStore getMetaDataStore();

}
