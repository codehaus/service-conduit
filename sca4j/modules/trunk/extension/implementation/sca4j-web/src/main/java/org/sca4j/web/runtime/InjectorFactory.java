/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
