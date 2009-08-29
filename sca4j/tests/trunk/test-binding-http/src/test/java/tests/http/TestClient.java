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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package tests.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 5444 $ $Date: 2008-09-19 15:57:03 +0100 (Fri, 19 Sep 2008) $
 */
public class TestClient extends TestCase {

    @Reference protected CardService cardService;

    public void testUpload() {
        assertEquals("ABCD uploaded with amount 1234.0", cardService.upload(new CardUploadRequest("ABCD", 1234)));
    }

    public void testDisable() {
        assertEquals("ABCD disabled", cardService.disable("ABCD"));
    }


    public void testFlatUploadEncoded() throws IOException {
        String encoded = "http://localhost:8900/card/upload?cardNumber=" + URLEncoder.encode("A,B@C D", "UTF-8") + "&amount=1234";
        URL serviceEndpoint = new URL(encoded);
        URLConnection c = serviceEndpoint.openConnection();
        assertEquals("A,B@C D uploaded with amount 1234.0", readResponseData(c));
    }

    public void testFlatUpload() throws IOException {
        URL serviceEndpoint = new URL("http://localhost:8900/card/upload?cardNumber=ABCD&amount=1234");
        URLConnection c = serviceEndpoint.openConnection();
        assertEquals("ABCD uploaded with amount 1234.0", readResponseData(c));
    }


    private String readResponseData(URLConnection c) throws IOException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            is = c.getInputStream();
            isr = new InputStreamReader(is);
            br= new BufferedReader(isr);

            StringBuilder responseData = new StringBuilder();
            String data = null;
            while((data=br.readLine()) != null) {
                responseData.append(data);
            }

            return responseData.toString();
        }
        finally {
            closeAllQuietly(is, isr, br);
        }
    }

    private void closeAllQuietly(InputStream is, Reader isr, Reader br) {
        try {
            if(br != null) {
                br.close();
            }

            if(isr != null) {
                isr.close();
            }

            if(is != null) {
                is.close();
            }
        }
        catch(IOException e) {
            //Irrelevant
        }
    }

}
