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
package org.sca4j.loader.composite;

import javax.xml.stream.XMLStreamReader;

import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.XmlValidationFailure;
import org.sca4j.scdl.ComponentDefinition;

/**
 * A validation failure indicating an attempt to configure a non-existent component service.
 *
 * @version $Rev: 4282 $ $Date: 2008-05-21 03:03:43 +0100 (Wed, 21 May 2008) $
 */
public class ComponentServiceNotFound extends XmlValidationFailure<ComponentDefinition> {
    private String serviceName;
    private ComponentDefinition definition;

    public ComponentServiceNotFound(String serviceName, ComponentDefinition definition, XMLStreamReader reader) {
        super("The component " + definition.getName() + " does not have a service " + serviceName, definition, reader);
        this.serviceName = serviceName;
        this.definition = definition;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ComponentDefinition getComponentDefinition() {
        return definition;
    }
}
