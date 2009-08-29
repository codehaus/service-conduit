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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.tests.binding.axis2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import javax.activation.DataHandler;
import javax.activation.DataSource;

import junit.framework.TestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 4924 $ $Date: 2008-06-29 10:42:38 +0100 (Sun, 29 Jun 2008) $
 */
public class Axis2EchoTest extends TestCase {
	
	/* @see AXIS2-3146 {jira}*/
	static {
		System.setProperty("http.nonProxyHosts", "");
	}

    @Reference
    protected Axis2EchoService service;

    @Reference
    protected Axis2FaultService faultService;

    private OMFactory factory;

    
    /**
     * Test WS operation with NO security
     * 
     */
    public void testEchoText() {
        OMElement message = getInputText();
        OMElement response = service.echoNoSecurity(message);
        verifyOutputText(response);
    }
    
    /**
     * Test WS operation with NO security and MTOM
     * 
     */
    public void testEchoDataWithMTOM() throws IOException {
        OMElement message = getInputMtom();
        OMElement response = service.echoNoSecurity(message);
        verifyOutputMtom(response);
    }
    
    /**
     * Test WS Operation with Username token security
     */
    public void testEchoTextWithUT() {
        OMElement message = getInputText();
        OMElement response = service.echoWsUsernameToken(message);
        verifyOutputText(response);
    }

    /**
     * Test WS operation with Username token and MTOM
     * 
     */
    public void testEchoDataWithUTMTOM() throws IOException {
        OMElement message = getInputMtom();
        OMElement response = service.echoWsUsernameToken(message);
        verifyOutputMtom(response);
    }
    
    /**
     * Test WS Operation with X509 token security
     */
    public void testEchoTextWithX509() {
        OMElement message = getInputText();
        OMElement response = service.echoWsX509Token(message);
        verifyOutputText(response);
    }
    
    /**
     * Test WS operation with Username token and MTOM
     * 
     */
    public void testEchoDataWithX509MTOM() throws IOException {
        OMElement message = getInputMtom();
        OMElement response = service.echoWsX509Token(message);
        verifyOutputMtom(response);
    }
    

    public void testRuntimeFault() {
        try {
            faultService.runtimeFaultOperation(getInputText());
            fail();
        } catch (ServiceUnavailableException e) {
            assertTrue(e.getMessage().contains("Runtime exception thrown from service"));
        }

    }

    private OMElement getInputText() {

        OMElement message = factory.createOMElement("data", null);
        OMText text = factory.createOMText(message, "Hello World");
        message.addChild(text);

        return message;

    }

    private void verifyOutputText(OMElement response) {
        String responseText = response.getText();
        assertEquals("Hello World", responseText);
    }

    private OMElement getInputMtom() {

        DataHandler dataHandler = new DataHandler(new DataSource() {
            public String getContentType() {
                return "text/dat";
            }

            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("Hello World".getBytes());
            }

            public String getName() {
                return null;
            }

            public OutputStream getOutputStream() throws IOException {
                return null;
            }
        });

        OMElement message = factory.createOMElement("data", null);
        OMText text = factory.createOMText(dataHandler, true);
        text.setOptimize(true);
        message.addChild(text);

        return message;

    }

    private void verifyOutputMtom(OMElement response) throws IOException {

        OMText responseText = (OMText) response.getFirstOMChild();
        responseText.setOptimize(true);
        DataHandler responseData = (DataHandler) responseText.getDataHandler();
        InputStream is = responseData.getInputStream();
        InputStreamReader reader = new InputStreamReader(is);
        char buffer[] = new char[1024];
        StringWriter writer = new StringWriter();
        for (int count; (count = reader.read(buffer, 0, buffer.length)) > 0;) {
            writer.write(buffer, 0, count);

        }
        assertEquals("Hello World", writer.toString());

    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = OMAbstractFactory.getOMFactory();
    }

}