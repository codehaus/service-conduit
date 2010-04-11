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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @version $Rev: 3690 $ $Date: 2008-04-22 20:06:52 +0100 (Tue, 22 Apr 2008) $
 */
public class OptimizedMBean<T> extends AbstractMBean {
    private final ObjectFactory<T> objectFactory;
    private final Map<String, Method> getters;
    private final Map<String, Method> setters;
    private final Map<OperationKey, Method> operations;

    public OptimizedMBean(ObjectFactory<T> objectFactory,
                          MBeanInfo mbeanInfo,
                          Map<String, Method> getters,
                          Map<String, Method> setters,
                          Map<OperationKey, Method> operations) {
        super(mbeanInfo);
        this.objectFactory = objectFactory;
        this.getters = getters;
        this.setters = setters;
        this.operations = operations;
    }

    public Object getAttribute(String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Method interceptor = getters.get(s);
        if (interceptor == null) {
            throw new AttributeNotFoundException(s);
        }
        return invoke(interceptor, null);
    }

    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        Method interceptor = setters.get(attribute.getName());
        if (interceptor == null) {
            throw new AttributeNotFoundException(attribute.getName());
        }
        invoke(interceptor, new Object[]{attribute.getValue()});
    }

    public Object invoke(String s, Object[] objects, String[] strings) throws MBeanException, ReflectionException {
        OperationKey operation = new OperationKey(s, strings);
        Method interceptor = operations.get(operation);
        if (interceptor == null) {
            throw new ReflectionException(new NoSuchMethodException(operation.toString()));
        }
        return invoke(interceptor, objects);
    }

    Object invoke(Method interceptor, Object[] args) throws MBeanException, ReflectionException {
        WorkContext workContext = new WorkContext();
        workContext.addCallFrame(new CallFrame());
        WorkContext oldContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            T instance = objectFactory.getInstance();
            return interceptor.invoke(instance, args);
        } catch (ObjectCreationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw new MBeanException((Exception) e.getCause());
            } else {
                throw new ReflectionException(e);
            }
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(oldContext);
        }
    }
}
