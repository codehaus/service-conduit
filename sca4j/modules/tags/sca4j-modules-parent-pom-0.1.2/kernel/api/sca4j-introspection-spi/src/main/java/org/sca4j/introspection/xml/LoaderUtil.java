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
package org.sca4j.introspection.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Utility functions to support loader implementations.
 *
 * @version $Rev: 3029 $ $Date: 2008-03-07 13:45:37 +0000 (Fri, 07 Mar 2008) $
 */
public final class LoaderUtil {
    private LoaderUtil() {
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested content.
     *
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    public static void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }

    /**
     * Construct a QName from an XML value.
     *
     * @param text             the text of an XML QName; if null or "" then null will be returned
     * @param defaultNamespace the default namespace to use if none is defined
     * @param context          the context for resolving namespace prefixes
     * @return a QName with the appropriate namespace set
     */
    public static QName getQName(String text, String defaultNamespace, NamespaceContext context) {
        if (text == null || text.length() == 0) {
            return null;
        }

        int index = text.indexOf(':');
        if (index < 1 || index == text.length() - 1) {
            // unqualifed form - use the default supplied
            return new QName(defaultNamespace, text);
        } else {
            String prefix = text.substring(0, index);
            String uri = context.getNamespaceURI(prefix);
            String localPart = text.substring(index + 1);
            return new QName(uri, localPart, prefix);
        }
    }

}
