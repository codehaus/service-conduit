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
package org.sca4j.transform.dom2java;

import java.net.MalformedURLException;
import java.net.URL;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.AbstractPullTransformer;

import org.w3c.dom.Node;

/**
 * String to URL Transformer
 */
public class String2URL extends AbstractPullTransformer<Node, URL> {
	
	private static final JavaClass<URL> TARGET = new JavaClass<URL>(URL.class);

	/**
	 * @see org.sca4j.transform.Transformer#getTargetType()
	 */
	public DataType<?> getTargetType() {
		return TARGET;
	}

	/**
	 * Transformation for URL
	 * 
	 * @see org.sca4j.transform.PullTransformer#transform(java.lang.Object, org.sca4j.transform.TransformContext)
	 */
	public URL transform(final Node node, final TransformContext context) throws TransformationException {
		final String content = node.getTextContent();
		final URL url;
		try {
			url = new URL(node.getTextContent());
		} catch (MalformedURLException me) {
			throw new TransformationException("Unable to create URL :- " + content, me);
		}
		return url;
	}
}
