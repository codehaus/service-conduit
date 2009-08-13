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
package org.sca4j.jpa.introspection;

import java.lang.reflect.Type;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContextType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.api.jpa.ConversationalDaoImpl;
import org.sca4j.host.Namespaces;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.java.introspection.JavaImplementationProcessor;
import org.sca4j.java.scdl.JavaImplementation;
import org.sca4j.jpa.scdl.PersistenceContextResource;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

/**
 * Implementation loader for JPA component.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class JpaImplementationLoader implements TypeLoader<JavaImplementation> {

    public static final QName IMPLEMENTATION_JPA = new QName(Namespaces.SCA4J_NS, "implementation.jpa");

    private final JavaImplementationProcessor implementationProcessor;
    private final ServiceContract<Type> factoryServiceContract;

    public JpaImplementationLoader(@Reference JavaImplementationProcessor implementationProcessor, @Reference ContractProcessor contractProcessor) {
        this.implementationProcessor = implementationProcessor;
        ValidationContext context = new DefaultValidationContext();
        factoryServiceContract = contractProcessor.introspect(new TypeMapping(), EntityManager.class, context);
        assert !context.hasErrors();  // should not happen
    }

    /**
     * Creates the instance of the implementation type.
     *
     * @param reader  Stax XML stream reader used for reading data.
     * @param context Introspection context.
     * @return An instance of the JPA implemenation.
     */
    public JavaImplementation load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);

        try {
            JavaImplementation implementation = new JavaImplementation();
            String persistenceUnit = reader.getAttributeValue(null, "persistenceUnit");
            if (persistenceUnit == null) {
                MissingAttribute failure = new MissingAttribute("Missing attribute: persistenceUnit", "persistenceUnit", reader);
                context.addError(failure);
                return implementation;
            }

            implementation.setImplementationClass(ConversationalDaoImpl.class.getName());

            URI contributionUri = context.getContributionUri();
            String targetNs = context.getTargetNamespace();
            ClassLoader cl = getClass().getClassLoader();

            IntrospectionContext childContext = new DefaultIntrospectionContext(contributionUri, cl, targetNs);
            implementationProcessor.introspect(implementation, childContext);
            if (childContext.hasErrors()) {
                context.addErrors(childContext.getErrors());
            }
            if (childContext.hasWarnings()) {
                context.addWarnings(childContext.getWarnings());
            }

            PojoComponentType pojoComponentType = implementation.getComponentType();

            PersistenceContextResource resource = new PersistenceContextResource(
                    "unit", persistenceUnit, PersistenceContextType.TRANSACTION, factoryServiceContract, false);
            FieldInjectionSite site = new FieldInjectionSite(ConversationalDaoImpl.class.getDeclaredField("entityManager"));
            pojoComponentType.add(resource, site);
            LoaderUtil.skipToEndElement(reader);

            return implementation;

        } catch (NoSuchFieldException e) {
            // this should not happen
            throw new AssertionError(e);
        }

    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!"persistenceUnit".equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }


}
