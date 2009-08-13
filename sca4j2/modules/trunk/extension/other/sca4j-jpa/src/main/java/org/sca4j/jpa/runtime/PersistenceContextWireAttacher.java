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
package org.sca4j.jpa.runtime;

import java.net.URI;
import javax.transaction.TransactionManager;

import org.osoa.sca.annotations.Reference;

import org.sca4j.jpa.provision.PersistenceContextWireTargetDefinition;
import org.sca4j.jpa.spi.classloading.EmfClassLoaderService;
import org.sca4j.jpa.spi.EmfBuilderException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches the target side of entity manager factories.
 *
 * @version $Revision$ $Date$
 */
public class PersistenceContextWireAttacher implements TargetWireAttacher<PersistenceContextWireTargetDefinition> {
    private EmfBuilder emfBuilder;
    private EmfClassLoaderService classLoaderService;
    private TransactionManager tm;
    private EntityManagerService emService;

    /**
     * Constructor.
     *
     * @param emService          the service for creating EntityManagers
     * @param tm                 the transaction manager
     * @param emfBuilder         the EMF builder
     * @param classLoaderService the classloader service for returning EMF classloaders
     */
    public PersistenceContextWireAttacher(@Reference EntityManagerService emService,
                                          @Reference TransactionManager tm,
                                          @Reference EmfBuilder emfBuilder,
                                          @Reference EmfClassLoaderService classLoaderService) {
        this.emfBuilder = emfBuilder;
        this.classLoaderService = classLoaderService;
        this.emService = emService;
        this.tm = tm;
    }

    public ObjectFactory<?> createObjectFactory(PersistenceContextWireTargetDefinition definition) throws WiringException {
        String unitName = definition.getUnitName();
        boolean extended = definition.isExtended();
        URI classLoaderUri = definition.getClassLoaderId();
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            // get the classloader for the entity manager factory
            ClassLoader appCl = classLoaderService.getEmfClassLoader(classLoaderUri);
            Thread.currentThread().setContextClassLoader(appCl);
            // eagerly build the the EntityManagerFactory
            emfBuilder.build(unitName, appCl);
            if (definition.isMultiThreaded()) {
                return new MultiThreadedEntityManagerProxyFactory(unitName, extended, emService, tm);
            } else {
                return new StatefulEntityManagerProxyFactory(unitName, extended, emService, tm);
            }
        } catch (EmfBuilderException e) {
            throw new WiringException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, PersistenceContextWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

}
