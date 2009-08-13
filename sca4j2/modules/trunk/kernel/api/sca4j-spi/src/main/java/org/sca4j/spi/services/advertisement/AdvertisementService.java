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
