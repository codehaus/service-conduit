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
