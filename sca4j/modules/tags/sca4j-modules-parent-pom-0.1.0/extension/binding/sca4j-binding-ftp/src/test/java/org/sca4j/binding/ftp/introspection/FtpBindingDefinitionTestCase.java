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

package org.sca4j.binding.ftp.introspection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.sca4j.binding.ftp.scdl.FtpBindingDefinition;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;

/**
 * Unit test which checks that the binding definition is created correctly from the binding loader
 * @version $Revision$ $Date$
 */
public class FtpBindingDefinitionTestCase extends TestCase {
    private static final String XML_NO_COMMANDS =
            "<sca4j:binding.ftp uri=\"ftp://foo.com/service\" xmlns:sca4j=\"urn:sca4j.org\"></sca4j:binding.ftp>";

    private static final String XML_COMMANDS =
            "<sca4j:binding.ftp uri=\"ftp://foo.com/service\" xmlns:sca4j=\"urn:sca4j.org\">\n" +
                    "   <commands>\n" +
                    "     <command>QUOTE test1</command>\n" +
                    "     <command>QUOTE test2</command>\n" +
                    "   </commands>\n" +
                    "</sca4j:binding.ftp>";

    private DefaultIntrospectionContext context;
    private FtpBindingLoader loader;

    /**
     * Tests parsing of a binding configuratiton which contains no commands
     * @throws Exception
     */
    public void testBindingNoCommandsParse() throws Exception {
        InputStream stream = new ByteArrayInputStream(XML_NO_COMMANDS.getBytes());
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
        reader.nextTag();
        FtpBindingDefinition definition = loader.load(reader, context);
        assertNotNull(definition.getTargetUri());
    }

    /**
     * Tests parsing of a binding configuratiton which contains quote commands
     * @throws Exception
     */

    public void testBindingCommandsParse() throws Exception {
        InputStream stream = new ByteArrayInputStream(XML_COMMANDS.getBytes());
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
        reader.nextTag();
        FtpBindingDefinition definition = loader.load(reader, context);
        List<String> commands = definition.getSTORCommands();
        assertEquals(2, commands.size());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = new DefaultIntrospectionContext((URI) null, null, null);

        LoaderHelper helper = EasyMock.createNiceMock(LoaderHelper.class);
        EasyMock.replay(helper);
        loader = new FtpBindingLoader(helper);

    }
}
