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
package org.sca4j.web.introspection;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.scdl.Implementation;

/**
 * Model object for a web component.
 *
 * @version $Rev: 956 $ $Date: 2007-08-31 15:35:28 -0700 (Fri, 31 Aug 2007) $
 */
public class WebImplementation extends Implementation<WebComponentType> {
    private static final long serialVersionUID = 5589199308230767243L;
    // the deprecated, F3-specific namespace
    @Deprecated
    public static final QName IMPLEMENTATION_WEBAPP = new QName(Namespaces.SCA4J_NS, "web");
    public static final QName IMPLEMENTATION_WEB = new QName(org.osoa.sca.Constants.SCA_NS, "implementation.web");

    public QName getType() {
        return IMPLEMENTATION_WEB;
    }

}
