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
