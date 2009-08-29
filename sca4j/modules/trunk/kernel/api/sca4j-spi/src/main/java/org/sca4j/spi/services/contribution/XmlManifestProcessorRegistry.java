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
package org.sca4j.spi.services.contribution;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * A registry of XmlElementManifestProcessors.
 *
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
public interface XmlManifestProcessorRegistry {
    /**
     * Register a XmlElementManifestProcessor using the processor's QName type as the key
     *
     * @param processor the processor to register
     */
    void register(XmlElementManifestProcessor processor);

    /**
     * Unregister an XmlElementManifestProcessor for a QName
     *
     * @param name the QName
     */
    void unregisterProcessor(QName name);

    /**
     * Dispatches to an XmlElementManifestProcessor based on the given Qname.
     *
     * @param name     the document element type to dispatch on
     * @param manifest the manifest being processed
     * @param reader   the reader position on the document element start tag
     * @param context  the context to which validation errors and warnings are reported
     * @throws ContributionException if an error occurs during processing
     */
    void process(QName name, ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException;

}
