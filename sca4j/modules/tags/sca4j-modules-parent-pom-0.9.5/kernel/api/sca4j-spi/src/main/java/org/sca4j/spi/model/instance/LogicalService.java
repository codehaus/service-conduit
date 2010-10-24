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
package org.sca4j.spi.model.instance;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasisopen.sca.Constants;
import org.sca4j.scdl.ComponentService;
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
    public List<QName> getIntents() {
        return definition.getIntents();
    }
    
    /**
     * @param intents Intents declared on the SCA artifact.
     */
    public void setIntents(List<QName> intents) {
        definition.setIntents(intents);
    }
    
    /**
     * @param intents Adds intents to the definition.
     */
    public void addIntents(List<QName> intents) {
        definition.addIntents(intents);
    }

    /**
     * @return Policy sets declared on the SCA artifact.
     */
    public List<QName> getPolicySets() {
        return definition.getPolicySets();
    }

    /**
     * @param policySets Policy sets declared on the SCA artifact.
     */
    public void setPolicySets(List<QName> policySets) {
        definition.setPolicySets(policySets);
    }
    
    /**
     * @param policySets Adds policy sets to the definition.
     */
    public void addPolicySets(List<QName> policySets) {
        definition.addPolicySets(policySets);
    }

    /**
     * Gets the component service for this logical service.
     * 
     * @return Component service if one was defined, otherwise null.
     */
    public ComponentService getComponentService() {
        return getParent().getDefinition().getServices().get(getDefinition().getName());
    }
}
