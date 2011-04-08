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
package org.sca4j.binding.sftp.introspection;

import static org.sca4j.binding.sftp.common.SftpBindingMetadata.DEFAULT_SFTP_PORT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.Constants;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.sftp.common.SftpBindingMetadata;
import org.sca4j.binding.sftp.scdl.SftpBindingDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * SFTP binding SCDL loader
 */
public class SftpBindingLoader implements TypeLoader<SftpBindingDefinition> {
    public static final QName BINDING_QNAME = new QName(Constants.SCA_NS, "binding.sftp");
    private final LoaderHelper loaderHelper;

    public SftpBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    public SftpBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        SftpBindingMetadata bindingMetadata = new SftpBindingMetadata();
        URI endpointUri = getEndpointUri(reader, introspectionContext);
        
        if (endpointUri != null) {
            bindingMetadata.host = endpointUri.getHost();
            bindingMetadata.port = endpointUri.getPort() == -1 ? DEFAULT_SFTP_PORT : endpointUri.getPort();
            bindingMetadata.remotePath = endpointUri.getPath();
        }
        
        bindingMetadata.tmpFileSuffix = reader.getAttributeValue(null, "tmpFileSuffix");
        final SftpBindingDefinition bd = new SftpBindingDefinition(endpointUri, bindingMetadata, loaderHelper.loadKey(reader));
        loaderHelper.loadPolicySetsAndIntents(bd, reader, introspectionContext);
        
        //SFTP binding wont work with authentication
        QName sftpAuthentication = new QName(Constants.SCA_NS, "authentication.message");
        if (!bd.getIntents().contains(sftpAuthentication)) {
            bd.getIntents().add(0, sftpAuthentication);
        }
        
        LoaderUtil.skipToEndElement(reader);
        return bd;
    }

    private URI getEndpointUri(XMLStreamReader reader, IntrospectionContext introspectionContext) {
        URI uri = null;
        String uriString = reader.getAttributeValue(null, "uri");

        if (uriString == null) {
            MissingAttribute failure = new MissingAttribute("A binding URI must be specified ", "uri", reader);
            introspectionContext.addError(failure);
        } else if (uriString != null) {
            if (!uriString.startsWith("sftp://") && !uriString.startsWith("SFTP://")) {
                uriString = "sftp://" + uriString;
            }

            try {
                uri = new URI(uriString);
            } catch (URISyntaxException e) {
                InvalidValue failure = new InvalidValue("Invalid SFTP URI configured in the binding. ", uriString, reader);
                introspectionContext.addError(failure);
            }
        }
        return uri;
    }
}
