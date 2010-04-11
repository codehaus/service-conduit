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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.loader.common;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.scdl.Property;
import org.w3c.dom.Document;

/**
 * Loads a property declaration from an XML-based assembly file
 *
 * @version $Rev: 5128 $ $Date: 2008-08-01 17:15:16 +0100 (Fri, 01 Aug 2008) $
 */
public class PropertyLoader implements TypeLoader<Property> {
    private static final String NAME = "name";
    private static final String MANY = "many";
    private static final String MUST_SUPPLY = "mustSupply";
    private static final String TYPE = "type";
    private static final QName XS_STRING = new QName(XMLConstants.XML_NS_URI, "string");

    private final LoaderHelper helper;

    public PropertyLoader(@Reference LoaderHelper helper) {
        this.helper = helper;
    }

    public Property load(XMLStreamReader reader, IntrospectionContext ctx) throws XMLStreamException {
        validateAttributes(reader, ctx);
        String name = reader.getAttributeValue(null, NAME);
        boolean many = Boolean.parseBoolean(reader.getAttributeValue(null, MANY));
        boolean mustSupply = Boolean.parseBoolean(reader.getAttributeValue(null, MUST_SUPPLY));
        Document value = helper.loadValue(reader);

        Property property = new Property();
        property.setRequired(mustSupply);
        property.setName(name);
        property.setXmlType(XS_STRING);
        property.setMany(many);
        property.setDefaultValue(value);
        return property;
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!NAME.equals(name) && !MANY.equals(name) && !MUST_SUPPLY.equals(name) && !TYPE.equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
