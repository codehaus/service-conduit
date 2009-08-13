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
