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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.system.introspection;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.loader.common.ComponentReferenceLoader;
import org.sca4j.loader.common.ComponentServiceLoader;
import org.sca4j.loader.common.PropertyLoader;
import org.sca4j.loader.composite.ComponentLoader;
import org.sca4j.loader.composite.CompositeLoader;
import org.sca4j.loader.composite.IncludeLoader;
import org.sca4j.loader.composite.PropertyValueLoader;
import org.sca4j.loader.composite.WireLoader;
import org.sca4j.loader.impl.DefaultLoaderHelper;
import org.sca4j.loader.impl.LoaderRegistryImpl;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * Factory class for an implementation of Loader that can handle system SCDL.
 * <p/>
 * This loader can handle a constrained version of SCDL for bootstrapping a runtime. The constraints are:
 * <pre>
 * <ul>
 * <li>The only implementation type allowed is system</li>
 * <li>The only service contract type is a Java interface found through introspection</li>
 * <li>Resolution of SCDL artifacts by QName is not supported; scdlLocation or scdlResource must be used</li>
 * </ul>
 * </pre>
 *
 * @version $Rev: 5261 $ $Date: 2008-08-25 02:07:34 +0100 (Mon, 25 Aug 2008) $
 */
public class BootstrapLoaderFactory {

    public static Loader createLoader(SystemImplementationProcessor processor, MonitorFactory monitorFactory, XMLFactory xmlFactory) {

        LoaderHelper loaderHelper = new DefaultLoaderHelper();

        LoaderRegistryImpl loader = new LoaderRegistryImpl(monitorFactory.getMonitor(LoaderRegistryImpl.Monitor.class), xmlFactory);
        Map<QName, TypeLoader<?>> loaders = new HashMap<QName, TypeLoader<?>>();

        WireLoader wireLoader = new WireLoader(loaderHelper);

        // loader for <composite> document
        loaders.put(CompositeLoader.COMPOSITE, compositeLoader(loader, wireLoader, loaderHelper));

        // loader for <implementation.system> element
        SystemImplementationLoader systemLoader = new SystemImplementationLoader(processor);
        loaders.put(SystemImplementation.IMPLEMENTATION_SYSTEM, systemLoader);

        loaders.put(CompositeLoader.WIRE, wireLoader);

        loader.setLoaders(loaders);
        return loader;
    }

    private static CompositeLoader compositeLoader(Loader loader, WireLoader wireLoader, LoaderHelper loaderHelper) {
        PropertyLoader propertyLoader = new PropertyLoader(loaderHelper);
        PropertyValueLoader propertyValueLoader = new PropertyValueLoader(loaderHelper);

        ComponentReferenceLoader componentReferenceLoader = new ComponentReferenceLoader(loader, loaderHelper);
        ComponentServiceLoader componentServiceLoader = new ComponentServiceLoader(loader, loaderHelper);
        ComponentLoader componentLoader = new ComponentLoader(loader,
                                                              propertyValueLoader,
                                                              componentReferenceLoader,
                                                              componentServiceLoader,
                                                              loaderHelper);

        IncludeLoader includeLoader = new IncludeLoader(loader, null);
        return new CompositeLoader(loader, includeLoader, propertyLoader, componentLoader, wireLoader, loaderHelper);
    }


}
