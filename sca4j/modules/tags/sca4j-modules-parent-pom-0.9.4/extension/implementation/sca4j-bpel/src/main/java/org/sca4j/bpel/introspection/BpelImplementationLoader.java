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
package org.sca4j.bpel.introspection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.model.BpelComponentTypeResourceElement;
import org.sca4j.bpel.model.BpelImplementation;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.ElementLoadFailure;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.xml.LoaderRegistry;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.MissingAttribute;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;

/**
 * Loads the BPEL implementation.
 *
 */
@EagerInit
public class BpelImplementationLoader implements TypeLoader<BpelImplementation> {
    
    @Reference public LoaderHelper loaderHelper;
    @Reference public MetaDataStore metaDataStore;
    @Reference public LoaderRegistry loaderRegistry;
    
    @Init
    public void start() {
        loaderRegistry.registerLoader(BpelImplementation.IMPLEMENTATION_BPEL, this);
    }
    
    @Override
    public BpelImplementation load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        
        BpelImplementation implementation = new BpelImplementation();
        String process = reader.getAttributeValue(null, "process");
        
        if (process == null) {
            MissingAttribute failure = new MissingAttribute("The class attribute was not specified", "class", reader);
            context.addError(failure);
            LoaderUtil.skipToEndElement(reader);
            return implementation;
        }
        
        loaderHelper.loadPolicySetsAndIntents(implementation, reader, context);
        LoaderUtil.skipToEndElement(reader);
        
        try {
            QName processName = LoaderUtil.getQName(process, context.getTargetNamespace(), reader.getNamespaceContext());
            BpelComponentTypeResourceElement element = metaDataStore.resolve(context.getContributionUri(), BpelComponentTypeResourceElement.class, processName, null);
            if (element == null) {
                MissingProcess failure = new MissingProcess("Process with qualified name not found: " + processName.toString(), processName, reader);
                context.addError(failure);
            }
            implementation.setComponentType(element.getComponentType());
        } catch (MetaDataStoreException e) {
            ElementLoadFailure failure = new ElementLoadFailure("Error loading element", e, reader);
            context.addError(failure);
            return null;
        }
        
        return implementation;
        
    }

}
