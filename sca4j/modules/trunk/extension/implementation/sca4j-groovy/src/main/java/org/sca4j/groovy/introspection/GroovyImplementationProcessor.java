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
package org.sca4j.groovy.introspection;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.IOException;
import java.net.URL;

import org.osoa.sca.annotations.Reference;
import org.sca4j.groovy.scdl.GroovyImplementation;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.introspection.java.ImplementationNotFoundException;
import org.sca4j.introspection.java.ImplementationProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.validation.MissingResource;

/**
 * @version $Rev: 4352 $ $Date: 2008-05-25 22:51:33 +0100 (Sun, 25 May 2008) $
 */
public class GroovyImplementationProcessor implements ImplementationProcessor<GroovyImplementation> {

    private final ClassWalker<GroovyImplementation> classWalker;
    private final HeuristicProcessor<GroovyImplementation> heuristic;
    private final IntrospectionHelper helper;

    public GroovyImplementationProcessor(@Reference(name = "classWalker")ClassWalker<GroovyImplementation> classWalker,
                                         @Reference(name = "heuristic")HeuristicProcessor<GroovyImplementation> heuristic,
                                         @Reference(name = "helper")IntrospectionHelper helper) {
        this.classWalker = classWalker;
        this.heuristic = heuristic;
        this.helper = helper;
    }

    public void introspect(GroovyImplementation implementation, IntrospectionContext context) {

        Class<?> implClass;
        try {
            implClass = loadImplementation(implementation, context);
        } catch (ClassNotFoundException e) {
            context.addError(new MissingResource("Groovy class not found: ", implementation.getClassName()));
            return;
        } catch (ImplementationNotFoundException e) {
            context.addError(new MissingResource("Groovy script not found: ", implementation.getScriptName()));
            return;
        } catch (IOException e) {
            context.addError(new InvalidGroovySource(implementation.getScriptName(), e));
            return;
        }
        if (implClass == null) {
            return;
        }
        PojoComponentType componentType = new PojoComponentType(implClass.getName());
        componentType.setScope("STATELESS");
        implementation.setComponentType(componentType);

        TypeMapping typeMapping = helper.mapTypeParameters(implClass);

        IntrospectionContext childContext = new DefaultIntrospectionContext(context, typeMapping);
        classWalker.walk(implementation, implClass, childContext);

        heuristic.applyHeuristics(implementation, implClass, childContext);
        if (childContext.hasErrors()) {
            context.addErrors(childContext.getErrors());
        }
        if (childContext.hasWarnings()) {
            context.addWarnings(childContext.getWarnings());
        }
    }


    /**
     * Introspects the implementation artiact.
     *
     * @param implementation the artifact to introspect
     * @param context        the context where errors are reported
     * @return the loaded implementation class, or null if there was an error introspecting it. Errors will be reported in the IntrospectionContext
     * @throws ClassNotFoundException if the class was not on the classpath
     * @throws java.io.IOException    if there was an error reading the Groovy script file
     * @throws ImplementationNotFoundException
     *                                if the Groovy script file is not found
     */
    private Class<?> loadImplementation(GroovyImplementation implementation, IntrospectionContext context)
            throws ClassNotFoundException, ImplementationNotFoundException, IOException {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            // Set TCCL to the extension classloader as implementations may need access to Groovy classes. Also, Groovy
            // dependencies such as Antlr use the TCCL.
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            GroovyClassLoader gcl = new GroovyClassLoader(context.getTargetClassLoader());

            // if user supplied a class name, use it as the implementation
            String className = implementation.getClassName();
            if (className != null) {
                return gcl.loadClass(className);
            }

            // if user supplied a script name, compile it and use the resulting class as the implementation
            String scriptName = implementation.getScriptName();
            if (scriptName != null) {
                URL scriptURL = gcl.getResource(scriptName);
                if (scriptURL == null) {
                    throw new ImplementationNotFoundException(scriptName);
                }
                GroovyCodeSource codeSource = new GroovyCodeSource(scriptURL);
                return gcl.parseClass(codeSource);
            }
            // we should not have been called without an implementation artifact
            throw new AssertionError();
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }
}
