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
import static javax.xml.XMLConstants.XML_NS_URI;
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
        JAXB_MAPPING.put("boolean", new QName(XML_NS_URI, "boolean"));
        JAXB_MAPPING.put("byte", new QName(XML_NS_URI, "byte"));
        JAXB_MAPPING.put("short", new QName(XML_NS_URI, "short"));
        JAXB_MAPPING.put("int", new QName(XML_NS_URI, "int"));
        JAXB_MAPPING.put("long", new QName(XML_NS_URI, "long"));
        JAXB_MAPPING.put("float", new QName(XML_NS_URI, "float"));
        JAXB_MAPPING.put("double", new QName(XML_NS_URI, "double"));
        JAXB_MAPPING.put(String.class.getName(), new QName(XML_NS_URI, "string"));
        JAXB_MAPPING.put(BigInteger.class.getName(), new QName(XML_NS_URI, "integer"));
        JAXB_MAPPING.put(BigDecimal.class.getName(), new QName(XML_NS_URI, "decimal"));
        JAXB_MAPPING.put(Calendar.class.getName(), new QName(XML_NS_URI, "dateTime"));
        JAXB_MAPPING.put(Date.class.getName(), new QName(XML_NS_URI, "dateTime"));
        JAXB_MAPPING.put(QName.class.getName(), new QName(XML_NS_URI, "QName"));
        JAXB_MAPPING.put(URI.class.getName(), new QName(XML_NS_URI, "string"));
        JAXB_MAPPING.put(XMLGregorianCalendar.class.getName(), new QName(XML_NS_URI, "anySimpleType"));
        JAXB_MAPPING.put(Duration.class.getName(), new QName(XML_NS_URI, "duration"));
        JAXB_MAPPING.put(Object.class.getName(), new QName(XML_NS_URI, "anyType"));
        JAXB_MAPPING.put(Image.class.getName(), new QName(XML_NS_URI, "base64Binary"));
        JAXB_MAPPING.put("javax.activation.DataHandler", new QName(XML_NS_URI, "base64Binary"));
        JAXB_MAPPING.put(Source.class.getName(), new QName(XML_NS_URI, "base64Binary"));
        JAXB_MAPPING.put(UUID.class.getName(), new QName(XML_NS_URI, "string"));
        JAXB_MAPPING.put(byte[].class.getName(), new QName(XML_NS_URI, "base64Binary"));
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
