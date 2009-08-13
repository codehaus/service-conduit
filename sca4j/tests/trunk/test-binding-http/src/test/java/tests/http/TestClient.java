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
