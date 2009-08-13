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
package org.sca4j.transform.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class Node2StringTest extends TestCase {

    public void testTransform() throws Exception {
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><policy xmlns=\"urn\">Test data</policy>";
        
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        
        Element el = doc.createElementNS("urn:sca4j.org", "policy");
        el.appendChild(doc.createTextNode("Test data"));
        
        String output = new Node2String().transform(el, null);
        assertEquals(expected, output);
    }

}
