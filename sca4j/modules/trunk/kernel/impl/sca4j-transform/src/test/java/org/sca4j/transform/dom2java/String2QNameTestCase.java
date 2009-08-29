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

package org.sca4j.transform.dom2java;

import javax.xml.namespace.QName;

import org.sca4j.transform.TransformationException;

/**
 * Tests String to QName Transform
 */
public class String2QNameTestCase extends BaseTransformTest {

	/**
	 * Test of converting String to QName
	 */
	public void testQNameTransform() {
		final String Q_NAME = "<string_to_qname>{http://f3.com/ns/fabric/test}f3</string_to_qname>";
		try {
			final QName qname= getStringToQName().transform(getNode(Q_NAME), null);
			assertNotNull(qname);
			assertEquals("{http://f3.com/ns/fabric/test}f3", qname.toString());
			assertEquals("http://f3.com/ns/fabric/test", qname.getNamespaceURI());
			assertEquals("f3", qname.getLocalPart());
		} catch (TransformationException te) {
			fail("Transform exception should not occur " + te);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}
	
	/**
	 * Test of converting String to QName
	 */
	public void testQNameTransformWithNamespace() {
		final String Q_NAME = "<string_to_qname xmlns:foo='http://f3.com/ns/fabric/test'>foo:f3</string_to_qname>";
		try {
			final QName qname= getStringToQName().transform(getNode(Q_NAME), null);
			assertNotNull(qname);
			assertEquals("{http://f3.com/ns/fabric/test}f3", qname.toString());
			assertEquals("http://f3.com/ns/fabric/test", qname.getNamespaceURI());
			assertEquals("f3", qname.getLocalPart());
			assertEquals("foo", qname.getPrefix());
		} catch (TransformationException te) {
			fail("Transform exception should not occur " + te);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}

	/**
	 * Test failure of converting String to QName
	 */
	public void testQNameTransformFailure() {
		final String Q_NAME = "<string_to_qname>{}</string_to_qname>";
		try {
			getStringToQName().transform(getNode(Q_NAME), null);
			fail("Should not reach here something wrong in [ String2QName ] code");
		} catch (TransformationException te) {
			assertNotNull(te);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(te.getCause().getClass()));
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}

	/**
	 * @return
	 */
	private String2QName getStringToQName() {
		return new String2QName();
	}
}
