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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;

import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.PropertyValue;
import org.sca4j.system.introspection.SystemImplementationProcessor;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * @version $Revision: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */
@EagerInit
public class FeatureLoader implements TypeLoader<ComponentDefinition> {

    // Qualified name of the root element.
    //private static final QName QNAME = new QName(Constants.FABRIC3_SYSTEM_NS, "feature");

    private final SystemImplementation featureImplementation;

    private final LoaderHelper helper;

    public FeatureLoader(@Reference SystemImplementationProcessor processor, @Reference LoaderHelper helper) {
        this.helper = helper;

        featureImplementation = new SystemImplementation(FeatureComponent.class.getName());
        IntrospectionContext context = new DefaultIntrospectionContext(getClass().getClassLoader(), null, null);
        processor.introspect(featureImplementation, context);
    }

    public ComponentDefinition load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {

        String name = reader.getAttributeValue(null, "name");
        Document value = helper.loadValue(reader);
        PropertyValue propertyValue = new PropertyValue("feature", null, value);

        ComponentDefinition<Implementation<?>> def = new ComponentDefinition<Implementation<?>>(name);
        def.setImplementation(featureImplementation);
        def.add(propertyValue);

        return def;

    }
}
