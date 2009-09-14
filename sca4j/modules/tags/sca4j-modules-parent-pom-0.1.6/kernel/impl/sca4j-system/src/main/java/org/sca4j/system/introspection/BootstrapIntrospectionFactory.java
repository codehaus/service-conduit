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
package org.sca4j.system.introspection;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.DefaultClassWalker;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.introspection.impl.annotation.DestroyProcessor;
import org.sca4j.introspection.impl.annotation.EagerInitProcessor;
import org.sca4j.introspection.impl.annotation.InitProcessor;
import org.sca4j.introspection.impl.annotation.MonitorProcessor;
import org.sca4j.introspection.impl.annotation.PropertyProcessor;
import org.sca4j.introspection.impl.annotation.ReferenceProcessor;
import org.sca4j.introspection.impl.contract.DefaultContractProcessor;
import org.sca4j.introspection.java.AnnotationProcessor;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * Instantiates an ImplementationProcessor for introspecting system components. System components are composite-scoped and support the standard SCA
 * lifecycle, including @Init, @Destroy, and @EagerInit.
 *
 * @version $Rev: 5261 $ $Date: 2008-08-25 02:07:34 +0100 (Mon, 25 Aug 2008) $
 */
public class BootstrapIntrospectionFactory {

    /**
     * Returns a new ImplementationProcessor for system components.
     *
     * @return a new ImplementationProcessor for system components
     */
    public static SystemImplementationProcessor createSystemImplementationProcessor() {
        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        ContractProcessor contractProcessor = new DefaultContractProcessor(helper);

        Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation, SystemImplementation>> processors =
                new HashMap<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation, SystemImplementation>>();

        // no constructor processor is needed as that is handled by heuristics
        processors.put(Property.class, new PropertyProcessor<SystemImplementation>(helper));
        processors.put(Reference.class, new ReferenceProcessor<SystemImplementation>(contractProcessor, helper));
        processors.put(EagerInit.class, new EagerInitProcessor<SystemImplementation>());
        processors.put(Init.class, new InitProcessor<SystemImplementation>());
        processors.put(Destroy.class, new DestroyProcessor<SystemImplementation>());
        processors.put(Monitor.class, new MonitorProcessor<SystemImplementation>(helper, contractProcessor));

        ClassWalker<SystemImplementation> classWalker = new DefaultClassWalker<SystemImplementation>(processors);

        // heuristics for system components
        SystemServiceHeuristic serviceHeuristic = new SystemServiceHeuristic(contractProcessor, helper);
        SystemConstructorHeuristic constructorHeuristic = new SystemConstructorHeuristic();
        SystemUnannotatedHeuristic unannotatedHeuristic = new SystemUnannotatedHeuristic(helper, contractProcessor);
        SystemHeuristic systemHeuristic = new SystemHeuristic(serviceHeuristic, constructorHeuristic, unannotatedHeuristic);

        return new SystemImplementationProcessorImpl(classWalker, systemHeuristic, helper);
    }

}
