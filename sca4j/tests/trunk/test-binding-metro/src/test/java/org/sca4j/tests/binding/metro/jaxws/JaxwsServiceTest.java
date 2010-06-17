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
package org.sca4j.tests.binding.metro.jaxws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import junit.framework.TestCase;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.tests.binding.metro.jaxb.EchoMessage;
import org.sca4j.tests.binding.metro.jaxb.JaxbService;
import org.sca4j.tests.binding.metro.mtom.MtomService;
import org.sca4j.tests.binding.metro.simple.SimpleService;

public class JaxwsServiceTest extends TestCase {
	@Reference
	protected SimpleService simpleService;
	@Reference
	protected JaxbService jaxbService;
	@Reference
	protected MtomService mtomService;

	public void testSimpleService() {
		final String greeting = "Hello World";
		final String response = simpleService.echoSimple(greeting);
		assertEquals(greeting, response);
	}
	
	public void testJaxbService(){
		final EchoMessage message = new EchoMessage("jaxb", "test");
		final EchoMessage response = jaxbService.echoJaxb(message);
		assertEquals("jaxb", response.getName());
		assertEquals("test", response.getInfo());
	}

	public void testMtomService() throws IOException {
		byte[] mtomData = "Hello World".getBytes();
		DataHandler data = getDataHandler(mtomData);
		final String repsonse = mtomService.uploadFile("mtom", data);
		assertEquals("mtom", repsonse);

//		final FileUploadResponse uploadJaxbResp = mtomService.uploadJaxb(new FileUploadRequest("mtom", getDataHandler(mtomData)));
//		assertEquals(mtomData, getData(uploadJaxbResp.getData()));
	}

	private DataHandler getDataHandler(final byte[] data) {
		return new DataHandler(new DataSource() {

			@Override
			public InputStream getInputStream() throws IOException {
				return new ByteArrayInputStream(data);
			}

			@Override
			public String getContentType() {
				return "text/dat";
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				return null;
			}

			@Override
			public String getName() {
				return null;
			}
		});
	}

	private byte[] getData(DataHandler dataHandler) throws IOException {
		final InputStream is = dataHandler.getInputStream();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(is);
		os.flush();
		return os.toByteArray();
	}
}
