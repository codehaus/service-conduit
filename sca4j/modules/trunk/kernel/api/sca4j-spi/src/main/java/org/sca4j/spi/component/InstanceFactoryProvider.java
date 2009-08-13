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
package org.sca4j.spi.component;

import java.lang.reflect.Type;

import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.spi.ObjectFactory;

/**
 * @version $Rev: 5236 $ $Date: 2008-08-20 19:46:18 +0100 (Wed, 20 Aug 2008) $
 */
public interface InstanceFactoryProvider<T> {
    /**
     * Return the implementation class.
     *
     * @return the implementation class.
     */
    Class<T> getImplementationClass();

    /**
     * Sets an object factory for an injection site.
     *
     * @param name          the injection site name
     * @param objectFactory the object factory
     */
    void setObjectFactory(InjectableAttribute name, ObjectFactory<?> objectFactory);

    /**
     * Sets an object factory for an injection site.
     *
     * @param name          the injection site name
     * @param objectFactory the object factory
     * @param key           the key for Map-based injection sites
     */
    void setObjectFactory(InjectableAttribute name, ObjectFactory<?> objectFactory, Object key);
    
    
    /**
     * Gets the Object Factory for an injection site
     * @param name of the injectable attribute
     * @return ObjectFactory
     */
    ObjectFactory<?> getObjectFactory(InjectableAttribute name);

    /**
     * Returns the type for the injection site
     *
     * @param injectionSite the injection site name
     * @return the required type
     */
    Class<?> getMemberType(InjectableAttribute injectionSite);

    /**
     * Returns the generic type for the injection site
     *
     * @param injectionSite the injection site name
     * @return the required type
     */
    Type getGenericType(InjectableAttribute injectionSite);

    /**
     * Create an instance factory that can be used to create component instances.
     *
     * @return a new instance factory
     */
    InstanceFactory<T> createFactory();
}
