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
package org.sca4j.pojo.instancefactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.reflection.ReflectiveInstanceFactoryProvider;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectionSite;

/**
 * Builds a reflection-based instance factory provider.
 *
 * @version $Date: 2008-09-01 22:48:11 +0100 (Mon, 01 Sep 2008) $ $Revision: 5318 $
 */
@EagerInit
public class ReflectiveInstanceFactoryBuilder<T> implements InstanceFactoryBuilder<ReflectiveInstanceFactoryProvider<T>, InstanceFactoryDefinition> {

    private final InstanceFactoryBuilderRegistry registry;
    private final InstanceFactoryBuildHelper helper;

    public ReflectiveInstanceFactoryBuilder(@Reference InstanceFactoryBuilderRegistry registry, @Reference InstanceFactoryBuildHelper helper) {
        this.registry = registry;
        this.helper = helper;
    }

    @Init
    public void init() {
        registry.register(InstanceFactoryDefinition.class, this);
    }

    public ReflectiveInstanceFactoryProvider<T> build(InstanceFactoryDefinition ifpd, ClassLoader cl) throws InstanceFactoryBuilderException {

        try {
            @SuppressWarnings("unchecked")
            Class<T> implClass = (Class<T>) helper.loadClass(cl, ifpd.getImplementationClass());
            Constructor<T> ctr = helper.getConstructor(implClass, ifpd.getConstructor());

            Map<InjectionSite, InjectableAttribute> injectionSites = ifpd.getConstruction();
            InjectableAttribute[] cdiSources = new InjectableAttribute[ctr.getParameterTypes().length];
            for (Map.Entry<InjectionSite, InjectableAttribute> entry : injectionSites.entrySet()) {
                InjectionSite site = entry.getKey();
                InjectableAttribute attribute = entry.getValue();
                ConstructorInjectionSite constructorSite = (ConstructorInjectionSite) site;
                cdiSources[constructorSite.getParam()] = attribute;
            }
            for (int i = 0; i < cdiSources.length; i++) {
                if (cdiSources[i] == null) {
                    String clazz = ctr.getName();
                    throw new InstanceFactoryBuilderException("No injection value for constructor parameter " + i + " in class " + clazz, clazz);
                }
            }

            Method initMethod = helper.getMethod(implClass, ifpd.getInitMethod());
            Method destroyMethod = helper.getMethod(implClass, ifpd.getDestroyMethod());

            Map<InjectionSite, InjectableAttribute> postConstruction = ifpd.getPostConstruction();
            List<InjectableAttribute> list = Arrays.asList(cdiSources);
            boolean reinjectable = ifpd.isReinjectable();

            return new ReflectiveInstanceFactoryProvider<T>(ctr, list, postConstruction, initMethod, destroyMethod, reinjectable, cl);
        } catch (ClassNotFoundException ex) {
            throw new InstanceFactoryBuilderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new InstanceFactoryBuilderException(ex);
        }
    }
}
