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
package org.sca4j.fabric.services.advertisement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.xml.namespace.QName;

import org.sca4j.spi.services.advertisement.AdvertisementListener;
import org.sca4j.spi.services.advertisement.AdvertisementService;

/**
 * Default implementation of the advertisment service.
 *
 * @version $Revsion$ $Date$
 */
public class DefaultAdvertismentService implements AdvertisementService {

    // Listeners
    private Set<AdvertisementListener> listeners = new CopyOnWriteArraySet<AdvertisementListener>();

    // Features
    private Set<QName> features = new CopyOnWriteArraySet<QName>();

    private Map<QName, String> transportInfo = new HashMap<QName, String>();

    public Set<QName> getFeatures() {
        return features;
    }

    public void addFeature(QName feature) {
        features.add(feature);
        for (AdvertisementListener listener : listeners) {
            listener.featureAdded(feature);
        }
    }

    public void removeFeature(QName feature) {
        features.remove(feature);
        for (AdvertisementListener listener : listeners) {
            listener.featureRemoved(feature);
        }
    }

    public void addListener(AdvertisementListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AdvertisementListener listener) {
        listeners.remove(listener);
    }

    public void addTransportMetadata(QName transport, String metaData) {
        transportInfo.put(transport, metaData);
    }

    public void removeTransportMetadata(QName transport) {
        transportInfo.remove(transport);
    }

    public Map<QName, String> getTransportInfo() {
        return transportInfo;
    }

}
