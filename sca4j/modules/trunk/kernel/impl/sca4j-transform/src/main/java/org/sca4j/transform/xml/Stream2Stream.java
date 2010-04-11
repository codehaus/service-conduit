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
package org.sca4j.transform.xml;

import static javax.xml.stream.XMLStreamConstants.CDATA;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.COMMENT;
import static javax.xml.stream.XMLStreamConstants.DTD;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.ENTITY_REFERENCE;
import static javax.xml.stream.XMLStreamConstants.PROCESSING_INSTRUCTION;
import static javax.xml.stream.XMLStreamConstants.SPACE;
import static javax.xml.stream.XMLStreamConstants.START_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.sca4j.scdl.DataType;
import org.sca4j.transform.AbstractPushTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
public class Stream2Stream extends AbstractPushTransformer<XMLStreamReader, XMLStreamWriter> {
    public DataType<?> getSourceType() {
        return null;
    }

    public DataType<?> getTargetType() {
        return null;
    }

    public void transform(XMLStreamReader reader, XMLStreamWriter writer, TransformContext context) throws TransformationException {
        try {
            int level = 0;
            do {
                switch (reader.next()) {
                case START_DOCUMENT:
                    writer.writeStartDocument(reader.getCharacterEncodingScheme(), reader.getVersion());
                    level += 1;
                    break;
                case START_ELEMENT:
                    writer.writeStartElement(reader.getPrefix(), reader.getLocalName(), reader.getNamespaceURI());
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String prefix = reader.getAttributePrefix(i);
                        String namespaceUri = reader.getAttributeNamespace(i);
                        String localName = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        writer.writeAttribute(prefix, namespaceUri, localName, value);
                    }
                    level += 1;
                    break;
                case CHARACTERS:
                case CDATA:
                    writer.writeCharacters(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                    break;
                case ENTITY_REFERENCE:
                    writer.writeEntityRef(reader.getText());
                    break;
                case END_ELEMENT:
                    writer.writeEndElement();
                    level -= 1;
                    break;
                case END_DOCUMENT:
                    writer.writeEndDocument();
                    return;
                case COMMENT:
                case SPACE:
                case PROCESSING_INSTRUCTION:
                case DTD:
                    // ignore these for now
                    break;
                }
            } while (level != 0);
        } catch (XMLStreamException e) {
            throw new TransformationException(e);
        }
    }
}
