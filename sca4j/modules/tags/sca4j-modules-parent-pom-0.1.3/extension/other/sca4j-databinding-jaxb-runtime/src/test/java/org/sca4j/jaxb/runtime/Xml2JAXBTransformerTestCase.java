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
package org.sca4j.jaxb.runtime;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.sca4j.jaxb.runtime.impl.Xml2JAXBTransformer;

/**
 * @version $Revision$ $Date$
 */
public class Xml2JAXBTransformerTestCase extends TestCase {
    private Xml2JAXBTransformer transformer;

    public void testMarshall() throws Exception {
        Object result = transformer.transform("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><foo/>", null);
        assertTrue(result instanceof Foo);
    }

    protected void setUp() throws Exception {
        super.setUp();
        JAXBContext context = JAXBContext.newInstance(Foo.class);
        transformer = new Xml2JAXBTransformer(context);
    }
}
