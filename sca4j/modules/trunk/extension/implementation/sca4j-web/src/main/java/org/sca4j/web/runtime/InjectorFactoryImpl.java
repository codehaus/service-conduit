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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sca4j.pojo.reflection.FieldInjector;
import org.sca4j.pojo.reflection.Injector;
import org.sca4j.pojo.reflection.MethodInjector;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.web.provision.WebContextInjectionSite;

/**
 * Default implementaiton of the InjectorFactory.
 *
 * @version $Revision$ $Date$
 */
public class InjectorFactoryImpl implements InjectorFactory {

    public void createInjectorMappings(Map<String, List<Injector<?>>> injectors,
                                       Map<String, Map<String, InjectionSite>> siteMappings,
                                       Map<String, ObjectFactory<?>> factories,
                                       ClassLoader classLoader) throws InjectionCreationException {
        for (Map.Entry<String, ObjectFactory<?>> entry : factories.entrySet()) {
            String siteName = entry.getKey();
            ObjectFactory<?> factory = entry.getValue();
            Map<String, InjectionSite> artifactMapping = siteMappings.get(siteName);
            if (artifactMapping == null) {
                throw new InjectionCreationException("Injection site not found for: " + siteName);
            }
            for (Map.Entry<String, InjectionSite> siteEntry : artifactMapping.entrySet()) {
                String artifactName = siteEntry.getKey();
                InjectionSite site = siteEntry.getValue();
                List<Injector<?>> injectorList = injectors.get(artifactName);
                if (injectorList == null) {
                    injectorList = new ArrayList<Injector<?>>();
                    injectors.put(artifactName, injectorList);
                }
                Injector<?> injector;
                if (site instanceof WebContextInjectionSite) {
                    injector = createInjector(siteName, factory, (WebContextInjectionSite) site);
                } else if (site instanceof FieldInjectionSite) {
                    injector = createInjector(factory, artifactName, (FieldInjectionSite) site, classLoader);
                } else if (site instanceof MethodInjectionSite) {
                    injector = createInjector(factory, artifactName, (MethodInjectionSite) site, classLoader);
                } else {
                    throw new UnsupportedOperationException("Unsupported injection site type: " + site.getClass());
                }
                injectorList.add(injector);
            }
        }
    }

    private Injector<?> createInjector(ObjectFactory<?> factory, String artifactName, MethodInjectionSite site, ClassLoader classLoader) {
        try {
            return new MethodInjector(getMethod(site, artifactName, classLoader), factory);
        } catch (ClassNotFoundException e) {
            throw new WebComponentStartException(e);
        } catch (NoSuchMethodException e) {
            throw new WebComponentStartException(e);
        }
    }

    private Injector<?> createInjector(ObjectFactory<?> factory, String artifactName, FieldInjectionSite site, ClassLoader classLoader) {
        try {
            return new FieldInjector(getField(site, artifactName, classLoader), factory);
        } catch (NoSuchFieldException e) {
            throw new WebComponentStartException(e);
        } catch (ClassNotFoundException e) {
            throw new WebComponentStartException(e);
        }
    }

    private Injector<?> createInjector(String referenceName, ObjectFactory<?> factory, WebContextInjectionSite site) {
        if (site.getContextType() == WebContextInjectionSite.ContextType.SERVLET_CONTEXT) {
            Injector<?> injector = new ServletContextInjector();
            injector.setObjectFactory(factory, referenceName);
            return injector;
        } else {
            Injector<?> injector = new HttpSessionInjector();
            injector.setObjectFactory(factory, referenceName);
            return injector;
        }
    }

    private Method getMethod(MethodInjectionSite methodSite, String implementationClass, ClassLoader classLoader)
            throws ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = classLoader.loadClass(implementationClass);
        return methodSite.getSignature().getMethod(clazz);
    }

    private Field getField(FieldInjectionSite site, String implementationClass, ClassLoader classLoader)
            throws NoSuchFieldException, ClassNotFoundException {
        Class<?> clazz = classLoader.loadClass(implementationClass);
        String name = site.getName();
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

}
