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
package org.sca4j.spi.services.advertisement;

import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

/**
 * Provides the abstraction for locally advertising capaibilities within a runtime. The capabilities are expressed as qualified names.
 *
 * @version $Revsion$ $Date$
 */
public interface AdvertisementService {

    /**
     * Returns the list of features available on the current node.
     *
     * @return List of features.
     */
    Set<QName> getFeatures();

    /**
     * Adds a feature to the current node.
     *
     * @param feature Feature to be added.
     */
    void addFeature(QName feature);

    /**
     * Removes a feature from the current node.
     *
     * @param feature Feature to be removed.
     */
    void removeFeature(QName feature);

    /**
     * Adds an advertismenet listener.
     *
     * @param listener Listener to be added.
     */
    void addListener(AdvertisementListener listener);

    /**
     * Removes an advertismenet listener.
     *
     * @param listener Listener to be removed.
     */
    void removeListener(AdvertisementListener listener);

    /**
     * Adds metadata for a binding transport supported by the runtime.
     *
     * @param transport the QName representing the transport
     * @param metaData  the transport metadata
     */
    void addTransportMetadata(QName transport, String metaData);

    /**
     * Removes the transport and its metadata from the list of supported binding transports.
     *
     * @param transport the QName representing the transport
     */
    void removeTransportMetadata(QName transport);

    /**
     * Returns the collection of transport metadata keyed by transport QName.
     *
     * @return the transport metadata
     */
    public Map<QName, String> getTransportInfo();

}
