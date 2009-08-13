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
package org.sca4j.spi.builder.interceptor;

import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;
import org.sca4j.spi.wire.Interceptor;

/**
 * Implementations return an interceptor, creating one if necessary
 *
 * @version $Rev: 585 $ $Date: 2007-07-25 21:18:55 +0100 (Wed, 25 Jul 2007) $
 */
public interface InterceptorBuilder<PID extends PhysicalInterceptorDefinition, I extends Interceptor> {

    /**
     * Return an interceptor for the given interceptor definition metadata
     *
     * @param definition metadata used for returning an interceptor
     * @return the interceptor
     * @throws BuilderException if an error ocurrs returning the interceptor
     */
    I build(PID definition) throws BuilderException;
    
}
