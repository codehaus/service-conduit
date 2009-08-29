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
package org.sca4j.pojo.reflection;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.InstanceFactory;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 5318 $ $Date: 2008-09-01 22:48:11 +0100 (Mon, 01 Sep 2008) $
 */
public class ReflectiveInstanceFactory<T> implements InstanceFactory<T> {
    private final ObjectFactory<T> constructor;
    private InjectableAttribute[] attributes;
    private final Injector<T>[] injectors;
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
    private final ClassLoader cl;
    private final boolean reinjectable;

    public ReflectiveInstanceFactory(ObjectFactory<T> constructor,
                                     InjectableAttribute[] attributes,
                                     Injector<T>[] injectors,
                                     EventInvoker<T> initInvoker,
                                     EventInvoker<T> destroyInvoker,
                                     boolean reinjectable,
                                     ClassLoader cl) {
        this.constructor = constructor;
        this.attributes = attributes;
        this.injectors = injectors;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
        this.reinjectable = reinjectable;
        this.cl = cl;
    }

    public InstanceWrapper<T> newInstance(WorkContext workContext) throws ObjectCreationException {
        // push the work context onto the thread when calling the user object
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        WorkContext oldContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            T instance = constructor.getInstance();
            if (injectors != null) {
                for (Injector<T> injector : injectors) {
                    injector.inject(instance);
                }
            }
            return new ReflectiveInstanceWrapper<T>(instance, reinjectable, cl, initInvoker, destroyInvoker, attributes, injectors);
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(oldContext);
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }
}
