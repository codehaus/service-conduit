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
 */
package org.sca4j.binding.file.introspection;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.Constants;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.file.common.FileBindingMetadata;
import org.sca4j.binding.file.scdl.FileBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * @version $Revision$ $Date$
 */
public class FileBindingLoader implements TypeLoader<FileBindingDefinition> {
    public static final QName BINDING_QNAME = new QName(Constants.SCA_NS, "binding.file");
    private final LoaderHelper loaderHelper;

    public FileBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    public FileBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        FileBindingMetadata bindingMetadata = new FileBindingMetadata();
        URI endpointUri = getUri(reader, introspectionContext, "uri", false);
        
        bindingMetadata.archiveUri = getUri(reader, introspectionContext, "archiveUri", false);
        bindingMetadata.filenamePattern = reader.getAttributeValue(null, "filenamePattern");
        bindingMetadata.acquireFileLock = Boolean.valueOf(reader.getAttributeValue(null, "acquireFileLock"));
        bindingMetadata.acquireEndpointLock = Boolean.valueOf(reader.getAttributeValue(null, "acquireEndpointLock"));
        final String pollingFreqString = reader.getAttributeValue(null, "pollingFrequency");
        bindingMetadata.pollingFrequency = pollingFreqString != null ? Long.valueOf(pollingFreqString) : 0L;
        bindingMetadata.archiveFileTimestampPattern = reader.getAttributeValue(null, "archiveFileTimestampPattern");
        bindingMetadata.tmpFileSuffix = reader.getAttributeValue(null, "tmpFileSuffix");
        
        final FileBindingDefinition bd = new FileBindingDefinition(endpointUri, bindingMetadata, loaderHelper.loadKey(reader));
        LoaderUtil.skipToEndElement(reader);
        return bd;
    }

    private URI getUri(XMLStreamReader reader, IntrospectionContext introspectionContext, String attributeName, boolean mandatory) {
        URI uri = null;
        String uriString = reader.getAttributeValue(null, attributeName);

        if (mandatory && uriString == null) {
            MissingAttribute failure = new MissingAttribute("A binding URI must be specified ", attributeName, reader);
            introspectionContext.addError(failure);
        } else if (uriString != null) {           
           /* if (!uriString.startsWith("file:///") && !uriString.startsWith("FILE:///")) {
                uriString = "file:///" + uriString;
            }*/

            try {
                uri = new URI(URLEncoder.encode(uriString.replace('\\', '/'), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                InvalidValue failure = new InvalidValue("The URI configured in the binding has an invalid encoding. ", uriString + "\n" + e, reader);
                introspectionContext.addError(failure);
            } catch (URISyntaxException e) {
                InvalidValue failure = new InvalidValue("Invalid File URI configured in the binding. ", uriString, reader);
                introspectionContext.addError(failure);
            }
        }
        return uri;
    }
}
