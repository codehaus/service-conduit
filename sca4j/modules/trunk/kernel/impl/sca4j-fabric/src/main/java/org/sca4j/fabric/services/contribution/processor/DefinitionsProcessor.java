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
package org.sca4j.fabric.services.contribution.processor;

import static org.osoa.sca.Constants.SCA_NS;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.XmlProcessor;
import org.sca4j.spi.services.contribution.XmlProcessorRegistry;
import org.sca4j.spi.services.contribution.XmlResourceElementLoader;

/**
 * Processes a contributed definitions file.
 *
 * @version $Rev: 4302 $ $Date: 2008-05-23 10:41:13 +0100 (Fri, 23 May 2008) $
 */
@EagerInit
public class DefinitionsProcessor implements XmlProcessor {
    private static final QName DEFINITIONS = new QName(SCA_NS, "definitions");
    private XmlResourceElementLoader loader;

    public DefinitionsProcessor(@Reference(name = "processorRegistry")XmlProcessorRegistry processorRegistry,
                                @Reference(name = "loader")XmlResourceElementLoader loader) {
        this.loader = loader;
        processorRegistry.register(this);
    }

    public QName getType() {
        return DEFINITIONS;
    }

    public void processContent(Contribution contribution, ValidationContext context, XMLStreamReader reader, ClassLoader cl)
            throws ContributionException {
        try {
            URI uri = contribution.getUri();
            assert contribution.getResources().size() == 1;
            Resource resource = contribution.getResources().get(0);
            loader.load(reader, uri, resource, context, cl);
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
        }
    }
}
