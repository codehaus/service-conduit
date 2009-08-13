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

package org.sca4j.idl.wsdl.version;

import java.net.URL;

import org.sca4j.idl.wsdl.version.WsdlVersionChecker.WsdlVersion;

import junit.framework.TestCase;

/**
 * @version $Revision: 137 $ $Date: 2007-05-31 08:31:25 +0100 (Thu, 31 May 2007) $
 */
public class DefaultWsdlVersionCheckerTest extends TestCase {
    
    private WsdlVersionChecker versionChecker = new DefaultWsdlVersionChecker();

    /**
     * Checks for version 1.1
     */
    public void testGetVersion1_1() {        
        URL url = getClass().getClassLoader().getResource("example_1_1.wsdl");
        assertEquals(WsdlVersion.VERSION_1_1, versionChecker.getVersion(url));
    }

    /**
     * Checks for version 2.0
     *
     */
    public void testGetVersion2_0() {     
        URL url = getClass().getClassLoader().getResource("example_2_0.wsdl");
        assertEquals(WsdlVersion.VERSION_2_0, versionChecker.getVersion(url));
    }

    /**
     * Checks for invalid WSDL
     *
     */
    public void testInvalidWsdl() {     
        URL url = getClass().getClassLoader().getResource("invalid.wsdl");
        try {
            versionChecker.getVersion(url);
            fail("Expected to fail");
        } catch(WsdlVersionCheckerException ignore) {
            //
        }
    }

}
