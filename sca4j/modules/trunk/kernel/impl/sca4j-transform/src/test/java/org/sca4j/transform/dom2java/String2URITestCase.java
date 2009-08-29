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

import java.net.URI;
import java.net.URISyntaxException;

import org.sca4j.transform.TransformationException;


/**
 * Tests String to URI Transform
 */
public class  String2URITestCase extends BaseTransformTest {

	/**
	 * Test for successful transformation from String to URI 
	 */
	public void testURITransformSuccess() {
		final String uriContent = "xmlns:f3";
		final String xml = "<string_to_uri>" + uriContent + "</string_to_uri>";
		
		try {
			final URI transformedURI = getStringToURI().transform(getNode(xml), null);
			assertNotNull(transformedURI);
			assertEquals(uriContent, transformedURI.toString());
		} catch (TransformationException te) {
			fail("TransformationException : - Should Not Occur" + te);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}
	
	/**
	 * Test for unsuccessful Conversion from String URI
	 */
	public void testURITransformationSuccess() {
		final String errorURIContent = "[[[[]]io9876^^^hasx";
		final String xml = "<string_to_urierror>" + errorURIContent + "</string_to_urierror>";
		
		try {
			getStringToURI().transform(getNode(xml), null);
			fail("Should not convert to URI");
		} catch (TransformationException te) {
			assertNotNull(te);
		    URISyntaxException.class.isAssignableFrom(te.getClass());
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}
	
	/**
	 * @return StringToURI
	 */
	private String2URI getStringToURI() {
		return new String2URI();
	}
}
