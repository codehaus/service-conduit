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
 */
package org.sca4j.jndi.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *  JAXB object for configuring jndi resources
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JndiResourceConfig {

	@XmlElement(name = "env") public boolean env; // Flag indicating default JNDI prefix is required
	@XmlElement(name = "jndi.name") public String jndiName; // JNDI name for datasource
    @XmlElement(name = "provider.url") public String providerUrl; // Provider url for JNDI lookup
    @XmlElement(name = "initial.context.factory") public String initialContextFactory; // Initial context factory for JNDI lookup
    @XmlElement(name = "security.protocol") public String securityProtocol; // Security protocol to be used 
    @XmlElement(name = "security.authentication") public String securityAuthentication; // Authentication mechanism to use
    @XmlElement(name = "security.principal") public String securityPrincipal; // Name of user/program doing the authentication
    @XmlElement(name = "security.credentials") public String securityCredentials; // Credentials of user/program doing the authentication

}