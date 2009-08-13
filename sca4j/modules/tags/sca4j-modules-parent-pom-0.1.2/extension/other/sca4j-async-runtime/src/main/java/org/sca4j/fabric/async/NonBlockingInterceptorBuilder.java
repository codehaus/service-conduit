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
package org.sca4j.fabric.async;

import org.osoa.sca.annotations.Reference;

import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.interceptor.InterceptorBuilder;

/**
 * Creates a non-blocking interceptor
 *
 * @version $Rev: 5150 $ $Date: 2008-08-03 13:04:06 +0100 (Sun, 03 Aug 2008) $
 */
public class NonBlockingInterceptorBuilder implements InterceptorBuilder<NonBlockingInterceptorDefinition, NonBlockingInterceptor> {
    private WorkScheduler scheduler;

    public NonBlockingInterceptorBuilder(@Reference WorkScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public NonBlockingInterceptor build(NonBlockingInterceptorDefinition definition) throws BuilderException {
        return new NonBlockingInterceptor(scheduler);
    }

}
