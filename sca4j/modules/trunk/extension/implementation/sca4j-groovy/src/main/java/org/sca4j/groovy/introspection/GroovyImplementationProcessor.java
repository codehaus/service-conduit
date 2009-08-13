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
package org.sca4j.groovy.introspection;

import java.io.IOException;
import java.net.URL;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
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
