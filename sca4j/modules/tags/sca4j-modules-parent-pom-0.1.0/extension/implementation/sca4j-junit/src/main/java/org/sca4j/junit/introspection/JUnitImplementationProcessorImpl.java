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
package org.sca4j.junit.introspection;

import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.introspection.java.ImplementationNotFoundException;
import org.sca4j.java.introspection.ImplementationArtifactNotFound;
import org.sca4j.junit.scdl.JUnitImplementation;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.validation.MissingResource;

/**
 * @version $Rev: 4352 $ $Date: 2008-05-25 22:51:33 +0100 (Sun, 25 May 2008) $
 */
public class JUnitImplementationProcessorImpl implements
		JUnitImplementationProcessor {
	private final ClassWalker<JUnitImplementation> classWalker;
	private final HeuristicProcessor<JUnitImplementation> heuristic;
	private final IntrospectionHelper helper;

	public JUnitImplementationProcessorImpl(@Reference(name = "classWalker")
	ClassWalker<JUnitImplementation> classWalker,
			@Reference(name = "heuristic")
			HeuristicProcessor<JUnitImplementation> heuristic,
			@Reference(name = "helper")
			IntrospectionHelper helper) {
		this.classWalker = classWalker;
		this.heuristic = heuristic;
		this.helper = helper;
	}

	public void introspect(JUnitImplementation implementation,IntrospectionContext context) {
		
		String implClassName = implementation.getImplementationClass();
		PojoComponentType componentType = new PojoComponentType(implClassName);
		componentType.setScope("STATELESS");
		implementation.setComponentType(componentType);

		ClassLoader cl = context.getTargetClassLoader();
		Class<?> implClass = null;
		try {
			implClass = helper.loadClass(implClassName, cl);
		} catch (ImplementationNotFoundException e) {
			context.addError(new MissingResource("JUnit test class not found on classpath: ",implClassName));
			Throwable cause = e.getCause();
			if (cause instanceof ClassNotFoundException || cause instanceof NoClassDefFoundError) {
				context.addError(new ImplementationArtifactNotFound(implClassName, e.getCause().getMessage()));
			} else {
				context.addError(new ImplementationArtifactNotFound(implClassName));
			}
			return;
		}
		TypeMapping typeMapping = helper.mapTypeParameters(implClass);

		IntrospectionContext childContext = new DefaultIntrospectionContext(
				context, typeMapping);
		classWalker.walk(implementation, implClass, childContext);

		heuristic.applyHeuristics(implementation, implClass, childContext);
		if (childContext.hasErrors()) {
			context.addErrors(childContext.getErrors());
		}
		if (childContext.hasWarnings()) {
			context.addWarnings(childContext.getWarnings());
		}

	}
}
