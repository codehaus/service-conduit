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
package org.sca4j.bpel.lightweight.introspection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.bpel.lightweight.model.PartnerLinkDefinition;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderUtil;
import org.sca4j.introspection.xml.TypeLoader;

public class PartnerLinkLoader implements TypeLoader<PartnerLinkDefinition> {

    @Override
    public PartnerLinkDefinition load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        String name = reader.getAttributeValue(null, "name");
        String val = reader.getAttributeValue(null, "partnerLinkType");
        QName partnerLinkType = LoaderUtil.getQName(val, null, reader.getNamespaceContext());
        String myRole = reader.getAttributeValue(null, "myRole");
        String partnerRole = reader.getAttributeValue(null, "partnerRole");
        return new PartnerLinkDefinition(name, partnerLinkType, myRole, partnerRole);
    }

}
