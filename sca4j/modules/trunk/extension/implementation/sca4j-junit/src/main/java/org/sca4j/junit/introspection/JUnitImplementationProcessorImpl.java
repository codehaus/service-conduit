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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
