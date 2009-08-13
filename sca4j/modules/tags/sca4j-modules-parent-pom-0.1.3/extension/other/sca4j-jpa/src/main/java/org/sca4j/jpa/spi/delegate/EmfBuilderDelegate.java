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
package org.sca4j.jpa.spi.delegate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.sca4j.jpa.spi.EmfBuilderException;

/**
 * Delegate interface for creating entity manager factories for adding provider specific hook-ins.
 *
 * @version $Revision$ $Date$
 */
public interface EmfBuilderDelegate {

    /**
     * Builds the entity managed factory.
     *
     * @param info           Persistence unit info.
     * @param classLoader    Classloader to use.
     * @param dataSourceName the data soruce name.
     * @return Entity manager factory.
     * @throws EmfBuilderException if an error building the factory occurs
     */
    EntityManagerFactory build(PersistenceUnitInfo info, ClassLoader classLoader, String dataSourceName) throws EmfBuilderException;

}
