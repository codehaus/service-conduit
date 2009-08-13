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
package org.sca4j.introspection.java;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.Implementation;

/**
 * Interface for processing an implementation to introspect its component type.
 *
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 * @param <I>            the type of Implementation an implementation can handle
 */
public interface ImplementationProcessor<I extends Implementation<? extends AbstractComponentType<?, ?, ?, ?>>> {

    /**
     * Introspects an implementation and derives the associated component type. If errors or warnings are encountered, they will be collated in the
     * IntrospectionContext.
     *
     * @param implementation the implmentation to inspect
     * @param context        the introspection context
     */
    void introspect(I implementation, IntrospectionContext context);
}
