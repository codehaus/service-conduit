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
package org.sca4j.java.introspection;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.java.scdl.JavaImplementation;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.Property;

/**
 * Heuristic that determines the XML type of Java properties.
 *
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class JavaDataTypeHeuristic implements HeuristicProcessor<JavaImplementation> {
    private static final Map<String, QName> JAXB_MAPPING;
    static {
        JAXB_MAPPING = new ConcurrentHashMap<String, QName>();
        JAXB_MAPPING.put("boolean", new QName(W3C_XML_SCHEMA_NS_URI, "boolean"));
        JAXB_MAPPING.put("byte", new QName(W3C_XML_SCHEMA_NS_URI, "byte"));
        JAXB_MAPPING.put("short", new QName(W3C_XML_SCHEMA_NS_URI, "short"));
        JAXB_MAPPING.put("int", new QName(W3C_XML_SCHEMA_NS_URI, "int"));
        JAXB_MAPPING.put("long", new QName(W3C_XML_SCHEMA_NS_URI, "long"));
        JAXB_MAPPING.put("float", new QName(W3C_XML_SCHEMA_NS_URI, "float"));
        JAXB_MAPPING.put("double", new QName(W3C_XML_SCHEMA_NS_URI, "double"));
        JAXB_MAPPING.put(String.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "string"));
        JAXB_MAPPING.put(BigInteger.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "integer"));
        JAXB_MAPPING.put(BigDecimal.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "decimal"));
        JAXB_MAPPING.put(Calendar.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "dateTime"));
        JAXB_MAPPING.put(Date.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "dateTime"));
        JAXB_MAPPING.put(QName.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "QName"));
        JAXB_MAPPING.put(URI.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "string"));
        JAXB_MAPPING.put(XMLGregorianCalendar.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "anySimpleType"));
        JAXB_MAPPING.put(Duration.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "duration"));
        JAXB_MAPPING.put(Object.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "anyType"));
        JAXB_MAPPING.put(Image.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "base64Binary"));
        JAXB_MAPPING.put("javax.activation.DataHandler", new QName(W3C_XML_SCHEMA_NS_URI, "base64Binary"));
        JAXB_MAPPING.put(Source.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "base64Binary"));
        JAXB_MAPPING.put(UUID.class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "string"));
        JAXB_MAPPING.put(byte[].class.getName(), new QName(W3C_XML_SCHEMA_NS_URI, "base64Binary"));
    }

    public void applyHeuristics(JavaImplementation implementation, Class<?> implClass, IntrospectionContext context) {

        PojoComponentType componentType = implementation.getComponentType();
        Map<String, Property> properties = componentType.getProperties();
        for (Map.Entry<InjectionSite, InjectableAttribute> entry : componentType.getInjectionSites().entrySet()) {
            InjectionSite site = entry.getKey();
            InjectableAttribute attribute = entry.getValue();
            if (InjectableAttributeType.PROPERTY != attribute.getValueType()) {
                continue;
            }

            Property property = properties.get(attribute.getName());
            if (property.getXmlType() != null) {
                continue;
            }

            property.setXmlType(getXmlType(site.getType()));
        }
    }

    QName getXmlType(String className) {
        return JAXB_MAPPING.get(className);
    }
}
