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
package org.sca4j.fabric.config;

import java.io.InputStream;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.sca4j.spi.config.ConfigService;
import org.w3c.dom.Document;

public class ConfigServiceImplITest extends TestCase {
    
    public void testConfig() throws XPathExpressionException {
        
        System.setProperty("FTP_USER", "fred.flintstone");
        System.setProperty("DB_USER", "barney.rubble");
        
        InputStream configStream = getClass().getClassLoader().getResourceAsStream("sca4j-config.xml");
        ConfigService systemConfig = new ConfigServiceImpl(configStream);
        
        assertEquals("fred.flintstone", systemConfig.getHostProperty("ftp.user"));
        assertEquals("password", systemConfig.getHostProperty("ftp.password"));
        
        Document domainConfig = systemConfig.getDomainConfig();
        
        assertEquals("barney.rubble", XPathFactory.newInstance().newXPath().evaluate("/domainConfig/testDsConfig/username", domainConfig));
        assertEquals("20", XPathFactory.newInstance().newXPath().evaluate("/domainConfig/thread.pool/@size", domainConfig));
        
    }

}
