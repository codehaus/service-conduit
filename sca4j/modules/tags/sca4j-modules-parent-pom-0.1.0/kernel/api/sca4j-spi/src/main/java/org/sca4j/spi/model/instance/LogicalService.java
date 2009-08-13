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
package org.sca4j.spi.model.instance;

import java.net.URI;
import java.util.Set;

import javax.xml.namespace.QName;

import org.osoa.sca.Constants;

import org.sca4j.scdl.ServiceDefinition;

/**
 * Represents a resolved service
 *
 * @version $Rev: 5280 $ $Date: 2008-08-26 15:57:57 +0100 (Tue, 26 Aug 2008) $
 */
public class LogicalService extends Bindable {
    private static final long serialVersionUID = -2417797075030173948L;

    private static final QName TYPE = new QName(Constants.SCA_NS, "service");

    private final ServiceDefinition definition;
    private URI promote;

    /**
     * Default constructor
     *
     * @param uri        the service uri
     * @param definition the service definition
     * @param parent     the service parent component
     */
    public LogicalService(URI uri, ServiceDefinition definition, LogicalComponent<?> parent) {
        super(uri, parent, TYPE);
        this.definition = definition;
    }

    /**
     * Returns the service definition for the logical service.
     *
     * @return the service definition for the logical service
     */
    public ServiceDefinition getDefinition() {
        return definition;
    }

    /**
     * Returns the component service uri promoted by this service.
     *
     * @return the component service uri promoted by this service
     */
    public URI getPromotedUri() {
        return promote;
    }

    /**
     * Sets the component service uri promoted by this service
     *
     * @param uri the component service uri promoted by this service
     */
    public void setPromotedUri(URI uri) {
        this.promote = uri;
    }

    /**
     * @return Intents declared on the SCA artifact.
     */
    public Set<QName> getIntents() {
        return definition.getIntents();
    }
    
    /**
     * @param intents Intents declared on the SCA artifact.
     */
    public void setIntents(Set<QName> intents) {
        definition.setIntents(intents);
    }
    
    /**
     * @param intents Adds intents to the definition.
     */
    public void addIntents(Set<QName> intents) {
        definition.addIntents(intents);
    }

    /**
     * @return Policy sets declared on the SCA artifact.
     */
    public Set<QName> getPolicySets() {
        return definition.getPolicySets();
    }

    /**
     * @param policySets Policy sets declared on the SCA artifact.
     */
    public void setPolicySets(Set<QName> policySets) {
        definition.setPolicySets(policySets);
    }
    
    /**
     * @param policySets Adds policy sets to the definition.
     */
    public void addPolicySets(Set<QName> policySets) {
        definition.addPolicySets(policySets);
    }

}
