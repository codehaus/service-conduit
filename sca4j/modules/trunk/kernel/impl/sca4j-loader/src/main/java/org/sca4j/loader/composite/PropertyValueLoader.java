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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.loader.composite;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.PropertyValue;
import org.sca4j.spi.model.type.XSDSimpleType;

/**
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class PropertyValueLoader implements TypeLoader<PropertyValue> {
    private static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();

    static {
        ATTRIBUTES.put("name", "name");
        ATTRIBUTES.put("source", "source");
        ATTRIBUTES.put("file", "file");
        ATTRIBUTES.put("type", "type");
        ATTRIBUTES.put("element", "element");
    }
    private final LoaderHelper helper;

    public PropertyValueLoader(@Reference LoaderHelper helper) {
        this.helper = helper;
    }

    public PropertyValue load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);
        String name = reader.getAttributeValue(null, "name");
        if (name == null || name.length() == 0) {
            MissingAttribute failure = new MissingAttribute("Missing name attribute", "name", reader);
            context.addError(failure);
            return null;
        }

        String source = reader.getAttributeValue(null, "source");
        String file = reader.getAttributeValue(null, "file");
        if (source != null) {
            LoaderUtil.skipToEndElement(reader);
            return new PropertyValue(name, source);
        } else if (file != null) {
            try {
                URI uri = new URI(file);
                if (!uri.isAbsolute()) {
                    uri = context.getSourceBase().toURI().resolve(uri);
                }
                LoaderUtil.skipToEndElement(reader);
                return new PropertyValue(name, uri);
            } catch (URISyntaxException e) {
                InvalidValue failure = new InvalidValue("File specified for property " + name + " is invalid: " + file, name, reader, e);
                context.addError(failure);
                return null;
            }
        } else {
            return loadInlinePropertyValue(name, reader, context);
        }
    }

    private PropertyValue loadInlinePropertyValue(String name, XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        DataType<QName> dataType;
        String type = reader.getAttributeValue(null, "type");
        String element = reader.getAttributeValue(null, "element");
        if (type != null) {
            if (element != null) {
                InvalidValue failure = new InvalidValue("Cannot supply both type and element for property: " + name, name, reader);
                context.addError(failure);
                return null;
            }
            // TODO support type attribute
            throw new UnsupportedOperationException();
        } else if (element != null) {
            // TODO support element attribute
            throw new UnsupportedOperationException();
        } else {
            dataType = new XSDSimpleType(Element.class, XSDSimpleType.STRING);
        }

        Document value = helper.loadValue(reader);
        return new PropertyValue(name, dataType, value);
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!ATTRIBUTES.containsKey(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
