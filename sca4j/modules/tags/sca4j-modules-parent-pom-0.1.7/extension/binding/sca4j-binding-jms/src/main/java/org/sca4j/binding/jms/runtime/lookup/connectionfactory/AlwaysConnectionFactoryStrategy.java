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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.sca4j.binding.jms.runtime.lookup.connectionfactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.sca4j.binding.jms.common.ConnectionFactoryDefinition;
import org.sca4j.binding.jms.common.SCA4JJmsException;

/**
 * The connection factory is never looked up, it is always created.
 * 
 * @version $Revision: 4186 $ $Date: 2008-05-13 04:19:40 +0100 (Tue, 13 May
 *          2008) $
 * 
 */
public class AlwaysConnectionFactoryStrategy implements ConnectionFactoryStrategy {

    /**
     * @see org.sca4j.binding.jms.runtime.lookup.connectionfactory.ConnectionFactoryStrategy#getConnectionFactory(org.sca4j.binding.jms.common.ConnectionFactoryDefinition,
     *      java.util.Hashtable)
     */
    public ConnectionFactory getConnectionFactory(ConnectionFactoryDefinition definition, Hashtable<String, String> env) {

        try {

            ConnectionFactory cf = (ConnectionFactory) Class.forName(definition.getName()).newInstance();
            Map<String, String> props = definition.getProperties();
            // TODO We may need to factor this into provider specific classes
            // rather than making the general assumption on bean style props
            for (PropertyDescriptor pd : Introspector.getBeanInfo(cf.getClass()).getPropertyDescriptors()) {
                String propName = pd.getName();
                String propValue = props.get(propName);
                Method writeMethod = pd.getWriteMethod();
                if (propValue != null && writeMethod != null) {
                    writeMethod.invoke(cf, propValue);
                }
            }

            return cf;

        } catch (InstantiationException ex) {
            throw new SCA4JJmsException("Unable to create connection factory", ex);
        } catch (IllegalAccessException ex) {
            throw new SCA4JJmsException("Unable to create connection factory", ex);
        } catch (ClassNotFoundException ex) {
            throw new SCA4JJmsException("Unable to create connection factory", ex);
        } catch (IntrospectionException ex) {
            throw new SCA4JJmsException("Unable to create connection factory", ex);
        } catch (InvocationTargetException ex) {
            throw new SCA4JJmsException("Unable to create connection factory", ex);
        }

    }

}
