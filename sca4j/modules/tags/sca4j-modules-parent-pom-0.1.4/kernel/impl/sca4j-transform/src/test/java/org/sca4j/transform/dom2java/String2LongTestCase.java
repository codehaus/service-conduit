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

import org.sca4j.transform.TransformationException;

/**
 * Tests String to Integer Transform
 */
public class  String2LongTestCase extends BaseTransformTest {

	/**
	 * Test of converting String to Long
	 */
	public void testLongTransform() {
		final String ANY_LONG = "9965732839230";
		final String xml = "<string_to_long>" + ANY_LONG + "</string_to_long>";
		try {
			final long convertedLong = getStringToLong().transform(getNode(xml), null);
			assertNotNull(convertedLong);
            assertEquals(9965732839230l, convertedLong);
		} catch (TransformationException te) {
			fail("Transform exception should not occur " + te);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}
	
	/**
	 * Test failure of converting String to Integer
	 */
	public void testLongTransformFailure() {
	    final String NON_INTEGER = "11l";
		final String xml = "<string_to_long>" + NON_INTEGER + "</string_to_long>";
		try {
			getStringToLong().transform(getNode(xml), null);
			fail("Should not reach here something wrong in [ String2Long ] code");
		} catch (TransformationException te) {
			assertNotNull(te);
			assertTrue(NumberFormatException.class.isAssignableFrom(te.getCause().getClass()));
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}

	/**
	 * @return
	 */
	private String2Long getStringToLong() {
		return new String2Long();
	}
}
