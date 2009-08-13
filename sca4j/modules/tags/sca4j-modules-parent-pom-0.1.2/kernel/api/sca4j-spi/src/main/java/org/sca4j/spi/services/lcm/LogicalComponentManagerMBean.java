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
package org.sca4j.spi.services.lcm;

import org.sca4j.api.annotation.Management;
import org.sca4j.scdl.Composite;

/**
 * @version $Rev: 4792 $ $Date: 2008-06-08 18:00:07 +0100 (Sun, 08 Jun 2008) $
 */
@Management
public interface LogicalComponentManagerMBean {

    /**
     * Returns the URI of this domain.
     *
     * @return the URI of this domain
     */
    String getDomainURI();

    /**
     * Returns the domain composite.
     * <p/>
     * The domain composite is a pseudo composite representing the active components in the domain.
     *
     * @return the domain composite
     */
    Composite getDomainComposite();
}
