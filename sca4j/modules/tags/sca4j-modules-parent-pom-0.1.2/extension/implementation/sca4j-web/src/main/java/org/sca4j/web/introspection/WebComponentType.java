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
package org.sca4j.web.introspection;

import java.util.HashMap;
import java.util.Map;

import org.sca4j.scdl.ComponentType;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectionSite;

/**
 * A component type representing a web component.
 *
 * @version $Revision$ $Date$
 */
public class WebComponentType extends ComponentType {
    private static final long serialVersionUID = 9213093177241637932L;
    private final Map<String, Map<InjectionSite, InjectableAttribute>> sites = new HashMap<String, Map<InjectionSite, InjectableAttribute>>();

    /**
     * Returns a mapping from artifact id (e.g. servlet or filter class name, servlet context, session context) to injection site/injectable attribute
     * pair
     *
     * @return the mapping
     */
    public Map<String, Map<InjectionSite, InjectableAttribute>> getInjectionSites() {
        return sites;
    }

    /**
     * Sets a mapping from artifact id to injection site/injectable attribute pair.
     *
     * @param artifactId the artifact id
     * @param site       the injeciton site
     * @param attribute  the injectable attribute
     */
    public void addMapping(String artifactId, InjectionSite site, InjectableAttribute attribute) {
        Map<InjectionSite, InjectableAttribute> mapping = sites.get(artifactId);
        if (mapping == null) {
            mapping = new HashMap<InjectionSite, InjectableAttribute>();
            sites.put(artifactId, mapping);
        }
        mapping.put(site, attribute);
    }
}
