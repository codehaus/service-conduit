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
package org.sca4j.jmx.runtime;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.IntrospectionException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.jmx.provision.JMXWireSourceDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 5257 $ $Date: 2008-08-23 21:35:57 +0100 (Sat, 23 Aug 2008) $
 */
@EagerInit
public class JMXWireAttacher implements SourceWireAttacher<JMXWireSourceDefinition> {

    private static final String DOMAIN = "f3-management";
    private final MBeanServer mBeanServer;
    private final String subDomain;

    public JMXWireAttacher(@Reference MBeanServer mBeanServer,
                           @Property(name = "subDomain", required=false)String subDomain) {
        this.mBeanServer = mBeanServer;
        this.subDomain = subDomain;
    }

    public void attachToSource(JMXWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

    public void detachFromSource(JMXWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new AssertionError();
    }

    public void attachObjectFactory(JMXWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target)
            throws WiringException {
        if (mBeanServer == null) {
            return;
        }

        URI uri = source.getUri();
        String component = UriHelper.getDefragmentedNameAsString(uri);
        String service = uri.getFragment();
        try {
            Class<?> managementInterface = getClass().getClassLoader().loadClass(source.getInterfaceName());
            ObjectName name = new ObjectName(DOMAIN + ":SubDomain=" + subDomain + ",type=service,component=\"" + component + "\",service=" + service);
            OptimizedMBean<?> mbean = createOptimizedMBean(objectFactory, managementInterface);
            if (!mBeanServer.isRegistered(name)) {
                mBeanServer.registerMBean(mbean, name);
            }
        } catch (JMException e) {
            throw new WiringException(e);
        } catch (ClassNotFoundException e) {
            throw new WiringException(e);
        }
    }

    private <T> OptimizedMBean<T> createOptimizedMBean(ObjectFactory<T> objectFactory, Class<?> service) throws IntrospectionException {
        String className = service.getName();
        Set<String> attributeNames = new HashSet<String>();
        Map<String, Method> getters = new HashMap<String, Method>();
        Map<String, Method> setters = new HashMap<String, Method>();
        Map<OperationKey, Method> operations = new HashMap<OperationKey, Method>();
        for (Method method : service.getMethods()) {
            switch (getType(method)) {
            case GETTER:
                String getterName = getAttributeName(method);
                attributeNames.add(getterName);
                getters.put(getterName, method);
                break;
            case SETTER:
                String setterName = getAttributeName(method);
                attributeNames.add(setterName);
                setters.put(setterName, method);
                break;
            case OPERATION:
                operations.put(new OperationKey(method), method);
                break;
            }
        }

        MBeanAttributeInfo[] mbeanAttributes = createAttributeInfo(attributeNames, getters, setters);
        MBeanOperationInfo[] mbeanOperations = createOperationInfo(operations.values());
        MBeanInfo mbeanInfo = new MBeanInfo(className, null, mbeanAttributes, null, mbeanOperations, null);
        return new OptimizedMBean<T>(objectFactory, mbeanInfo, getters, setters, operations);
    }

    private MBeanOperationInfo[] createOperationInfo(Collection<Method> operations) {
        MBeanOperationInfo[] mbeanOperations = new MBeanOperationInfo[operations.size()];
        int i = 0;
        for (Method method : operations) {
            mbeanOperations[i++] = new MBeanOperationInfo(null, method);
        }
        return mbeanOperations;
    }

    private MBeanAttributeInfo[] createAttributeInfo(Set<String> attributeNames, Map<String, Method> getters, Map<String, Method> setters)
            throws IntrospectionException {
        MBeanAttributeInfo[] mbeanAttributes = new MBeanAttributeInfo[attributeNames.size()];
        int i = 0;
        for (String name : attributeNames) {
            mbeanAttributes[i++] = new MBeanAttributeInfo(name, null, getters.get(name), setters.get(name));
        }
        return mbeanAttributes;
    }

    private static enum MethodType {
        GETTER, SETTER, OPERATION
    }

    private static MethodType getType(Method method) {
        String name = method.getName();
        Class<?> returnType = method.getReturnType();
        int paramCount = method.getParameterTypes().length;

        if (Void.TYPE.equals(returnType) && name.length() > 3 && name.startsWith("set") && paramCount == 1) {
            return MethodType.SETTER;
        } else if (Boolean.TYPE.equals(returnType) && name.length() > 2 && name.startsWith("is") && paramCount == 0) {
            return MethodType.GETTER;
        } else if (name.length() > 3 && name.startsWith("get") && paramCount == 0) {
            return MethodType.GETTER;
        } else {
            return MethodType.OPERATION;
        }
    }

    private static String getAttributeName(Method method) {
        String name = method.getName();
        if (name.startsWith("is")) {
            return name.substring(2);
        } else {
            return name.substring(3);
        }
    }
}
