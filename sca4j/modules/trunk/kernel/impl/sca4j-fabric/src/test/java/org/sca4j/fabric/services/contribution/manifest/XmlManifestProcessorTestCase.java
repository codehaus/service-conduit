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
package org.sca4j.fabric.services.contribution.manifest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.host.expression.ExpressionExpander;
import org.sca4j.host.expression.ExpressionExpansionException;
import org.sca4j.services.xmlfactory.impl.XMLFactoryImpl;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.scdl.DefaultValidationContext;

/**
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
public class XmlManifestProcessorTestCase extends TestCase {
    public static final QName QNAME = new QName("foo", "bar");
    public static final byte[] XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bar xmlns=\"foo\"/>".getBytes();
    public static final byte[] XML_DTD = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<!DOCTYPE bar>" +
            "<bar xmlns=\"foo\"/>").getBytes();

    private XmlManifestProcessor processor;
    private XmlManifestProcessorRegistry registry;

    public void testDispatch() throws Exception {
        InputStream stream = new ByteArrayInputStream(XML);
        ContributionManifest manifest = new ContributionManifest();
        ValidationContext context = new DefaultValidationContext();
        processor.process(manifest, stream, context);
        EasyMock.verify(registry);
    }

    public void testDTDDispatch() throws Exception {
        InputStream stream = new ByteArrayInputStream(XML_DTD);
        ContributionManifest manifest = new ContributionManifest();
        ValidationContext context = new DefaultValidationContext();
        processor.process(manifest, stream, context);
        EasyMock.verify(registry);
    }

    protected void setUp() throws Exception {
        super.setUp();
        XMLFactory factory = new XMLFactoryImpl(new ExpressionExpander() {
            public String expand(String value) throws ExpressionExpansionException {
                return value;
            }
        });
        registry = EasyMock.createMock(XmlManifestProcessorRegistry.class);
        registry.process(EasyMock.eq(QNAME),
                         EasyMock.isA(ContributionManifest.class),
                         EasyMock.isA(XMLStreamReader.class),
                         EasyMock.isA(ValidationContext.class));
        EasyMock.replay(registry);
        processor = new XmlManifestProcessor(null, registry, factory);


    }
}
