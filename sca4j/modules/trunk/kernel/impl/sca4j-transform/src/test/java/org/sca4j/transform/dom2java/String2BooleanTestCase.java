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


/**
 * Tests String to boolean Transform
 */
public class  String2BooleanTestCase extends BaseTransformTest {

	/**
	 * Test of converting String to Boolean on true
	 */
	public void testBooleanTransformForTrue() {
		final String TRUE = "true";
		final String xml = "<string_to_boolean>" + TRUE + "</string_to_boolean>";
		try {
			final boolean convBoolean = getStringToBoolean().transform(getNode(xml), null);
			assertNotNull(convBoolean);
            assertTrue(convBoolean);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}
	
	/**
	 * Test failure of converting String to boolean on False
	 */
	public void testBooleanTransformForFalse() {
	    final String FALSE = "false";
		final String xml = "<string_to_boolean>" + FALSE + "</string_to_boolean>";
		try {
			boolean convBoolean = getStringToBoolean().transform(getNode(xml), null);
			assertNotNull(convBoolean);
			assertFalse(convBoolean);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}
	
	/**
	 * Test failure of converting String to boolean on False
	 */
	public void testBooleanOnUnspecifiedFalse() {
	    final String FALSE = "SHOULD BE FALSE";
		final String xml = "<string_to_boolean>" + FALSE + "</string_to_boolean>";
		try {
			boolean convBoolean = getStringToBoolean().transform(getNode(xml), null);
			assertNotNull(convBoolean);
			assertFalse(convBoolean);
		} catch (Exception e) {
			fail("Unexpexcted Exception Should not occur " + e);
		}
	}

	/**
	 * @return StringToBoolean
	 */
	private String2Boolean getStringToBoolean() {
		return new String2Boolean();
	}
}
