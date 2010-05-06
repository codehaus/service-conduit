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
package org.sca4j.tutorial.order.web;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

public class OrderTest extends TestCase {
    
    public void testOrder() throws Exception {
        
        URL url = new URL("http://localhost:8900/tutorial-web/order?productName=Pizza&address=ABC&creditCard=PQR");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Reader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder result = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            result.append((char)ch);
        }
        reader.close();
        assertEquals(200, connection.getResponseCode());
        assertEquals("Order placed true", result.toString());
        
    }

}
