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
package org.sca4j.web.introspection;

import java.util.List;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionException;

/**
 * Introspects a web.xml descriptor.
 *
 * @version $Revision$ $Date$
 */
public interface WebXmlIntrospector {

    /**
     * Returns the loaded classes for servlets, filters, and listeners configured in the web.xml. Errors will be collated in the
     * IntrospectionContext.
     *
     * @param context the introspection context. Classes will be loaded in the target classloader associated with the context.
     * @return the collection of loaded classes
     */
    List<Class<?>> introspectArtifactClasses(IntrospectionContext context);

}
