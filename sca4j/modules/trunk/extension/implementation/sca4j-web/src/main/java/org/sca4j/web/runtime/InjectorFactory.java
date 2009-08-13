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
package org.sca4j.web.runtime;

import java.util.List;
import java.util.Map;

import org.sca4j.pojo.reflection.Injector;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.scdl.InjectionSite;

/**
 * Creates Injector collections for injecting references, properties and context proxies into web application artifacts. These include servlets,
 * filters, the servlet context, and the session context.
 *
 * @version $Revision$ $Date$
 */
public interface InjectorFactory {
    /**
     * Populates a map of Injectors for each injectable artifact (servlet, filter, servlet context or session context) in the  web application.
     *
     * @param injectors    the map to populate, keyed by artifact id (e.g. servlet class name)
     * @param siteMappings a map keyed by site name (e.g. a reference or property name). The value is a map keyed by injectable artifact id with a
     *                     value containing a description of the injection site. For example, a reference may be injected on fields of multiple
     *                     servlets. This would be represented by an entry keyed on reference name with a value of a map keyed by servlet class name
     *                     and values containing injection site descriptions of the servlet fields.
     * @param factories    the object factories that supply injected values.
     * @param classLoader  the classloader to load classes in for the web application
     * @throws InjectionCreationException if an error occurs creating the injectors.
     */
    void createInjectorMappings(Map<String, List<Injector<?>>> injectors,
                                Map<String, Map<String, InjectionSite>> siteMappings,
                                Map<String, ObjectFactory<?>> factories,
                                ClassLoader classLoader) throws InjectionCreationException;
}
