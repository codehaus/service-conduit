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

import org.sca4j.introspection.xml.XmlValidationFailure;
import org.sca4j.scdl.Property;

/**
 * @version $Rev: 5384 $ $Date: 2008-09-11 03:43:35 +0100 (Thu, 11 Sep 2008) $
 */
public class RequiredPropertyNotProvided extends XmlValidationFailure<Property> {
    private String componentName;

    public RequiredPropertyNotProvided(Property property, String componentName, XMLStreamReader reader) {
        super("Component " + componentName + " has a required property " + property.getName() + " that is not set", property, reader);
        this.componentName = componentName;
    }

    public String getComponentName() {
        return componentName;
    }
}
