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
package org.sca4j.junit.scdl;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Implementation;

/**
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class JUnitImplementation extends Implementation<PojoComponentType> {
    public static final QName IMPLEMENTATION_JUNIT = new QName(Namespaces.SCA4J_NS, "junit");
    private static final long serialVersionUID = -5048471724313487914L;
    private String implementationClass;

    /**
     * Constructor supplying the name of the JUnit test class
     *
     * @param className the name of the JUnit test class
     */
    public JUnitImplementation(String className) {
        this.implementationClass = className;
    }

    public QName getType() {
        return IMPLEMENTATION_JUNIT;
    }

    /**
     * Returns the name of the JUnit test class.
     *
     * @return the name of the JUnit test class
     */
    public String getImplementationClass() {
        return implementationClass;
    }

}
