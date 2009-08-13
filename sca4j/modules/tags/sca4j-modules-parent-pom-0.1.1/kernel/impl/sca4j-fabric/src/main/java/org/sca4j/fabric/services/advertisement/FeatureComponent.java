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

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.services.advertisement.AdvertisementService;

/**
 * @version $Revsion$ $Date$
 */
@EagerInit
public class FeatureComponent {

    // Feature // TODO support list of features
    private String feature;

    // Advertisement service
    private AdvertisementService advertisementService;

    /**
     * @param advertisementService Advertisement se
     */
    @Reference
    public void setAdvertisementService(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    /**
     * @param feature Feature injected as a property.
     */
    @Property
    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * Registers the feature with the advertisement service.
     */
    @Init
    public void start() {
        advertisementService.addFeature(QName.valueOf(feature));
    }

}
