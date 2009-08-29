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
package org.sca4j.binding.ftp.introspection;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.ftp.scdl.FtpBindingDefinition;
import org.sca4j.binding.ftp.scdl.TransferMode;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.InvalidValue;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;

/**
 * @version $Revision$ $Date$
 */
public class FtpBindingLoader implements TypeLoader<FtpBindingDefinition> {

    private final LoaderHelper loaderHelper;

    /**
     * Constructor.
     *
     * @param loaderHelper the policy helper
     */
    public FtpBindingLoader(@Reference LoaderHelper loaderHelper) {
        this.loaderHelper = loaderHelper;
    }

    public FtpBindingDefinition load(XMLStreamReader reader, IntrospectionContext introspectionContext) throws XMLStreamException {
        validateAttributes(reader, introspectionContext);

        FtpBindingDefinition bindingDefinition = null;
        String uriString = null;

        try {

            bindingDefinition = createBindingDefinition(reader, introspectionContext);

        } catch (URISyntaxException ex) {
            InvalidValue failure = new InvalidValue("Invalid FTP URI configured in the binding. ", uriString, reader);
            introspectionContext.addError(failure);
        } catch (UnsupportedEncodingException e) {
            InvalidValue failure = new InvalidValue("The Uri configured in the binding has an invalid encoding. ",uriString + "\n" + e, reader);
            introspectionContext.addError(failure);
        }

        LoaderUtil.skipToEndElement(reader);
        return bindingDefinition;

    }

    /* Creates a binding definition from the binding configuration */
    private FtpBindingDefinition createBindingDefinition(XMLStreamReader reader,
            IntrospectionContext introspectionContext) throws UnsupportedEncodingException, URISyntaxException {
        FtpBindingDefinition bindingDefinition;
        URI endpointUri = getEndpointUri(reader, introspectionContext);
        TransferMode transferMode = getTransferMode(reader);
        String tmpFileSuffix = reader.getAttributeValue(null, "tmpFileSuffix");
        
        bindingDefinition = new FtpBindingDefinition(endpointUri, transferMode, loaderHelper.loadKey(reader));
        
        if(tmpFileSuffix != null) {
            bindingDefinition.setTmpFileSuffix(tmpFileSuffix);
        }

        loaderHelper.loadPolicySetsAndIntents(bindingDefinition, reader, introspectionContext);
        return bindingDefinition;
    }

    /* Returns back the transfer mode configured in the binding */
    private TransferMode getTransferMode(XMLStreamReader reader) {
        String transferMode = reader.getAttributeValue(null, "mode");
        return transferMode != null ? TransferMode.valueOf(transferMode) : TransferMode.PASSIVE;
    }

    /* Returns back a URI based on the uri attribute configured in the binding */
    private URI getEndpointUri(XMLStreamReader reader, IntrospectionContext introspectionContext) throws UnsupportedEncodingException, URISyntaxException {
        String uriString;
        uriString = reader.getAttributeValue(null, "uri");
         
        if (uriString == null) {
            MissingAttribute failure = new MissingAttribute("A binding URI must be specified ","uri", reader);
            introspectionContext.addError(failure);
            return null;
        }
        if (!uriString.startsWith("ftp://") && !uriString.startsWith("FTP://")) {
            uriString = "ftp://" + uriString;
        }
        // encode the URI since there may be expressions (e.g. "${..}") contained in it
        return new URI(URLEncoder.encode(uriString, "UTF-8"));
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!"uri".equals(name) && !"requires".equals(name) && !"policySets".equals(name) && !"mode".equals(name) && 
                !"tmpFileSuffix".equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
