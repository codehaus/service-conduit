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
package org.sca4j.web.provision;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import org.sca4j.scdl.InjectionSite;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;

/**
 * @version $Rev: 2803 $ $Date: 2008-02-17 05:57:55 -0800 (Sun, 17 Feb 2008) $
 */
public class WebComponentDefinition extends PhysicalComponentDefinition {
    private URI classLoaderId;
    private URI contributionUri;
    private String contextUrl;
    // map of resource id to injection site name/InjectionSite pair
    private Map<String, Map<String, InjectionSite>> injectionSiteMappings = new HashMap<String, Map<String, InjectionSite>>();
    private final Map<String, Document> propertyValues = new HashMap<String, Document>();

    /**
     * Gets the classloader id.
     *
     * @return Classloader id.
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Set the classloader id.
     *
     * @param classLoaderId Classloader id.
     */
    public void setClassLoaderId(URI classLoaderId) {
        this.classLoaderId = classLoaderId;
    }

    public URI getContributionUri() {
        return contributionUri;
    }

    public void setContributionUri(URI contributionUri) {
        this.contributionUri = contributionUri;
    }

    public Map<String, Map<String, InjectionSite>> getInjectionSiteMappings() {
        return injectionSiteMappings;
    }

    public void setInjectionMappings(Map<String, Map<String, InjectionSite>> mappings) {
        injectionSiteMappings = mappings;
    }

    public String getContextUrl() {
        return contextUrl;
    }

    public void setContextUrl(String contextUrl) {
        this.contextUrl = contextUrl;
    }

    public Map<String, Document> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValue(String name, Document value) {
        propertyValues.put(name, value);
    }

}
