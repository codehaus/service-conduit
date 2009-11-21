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
package org.sca4j.fabric.services.contribution.manifest;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.spi.services.contribution.MavenImport;

/**
 * Loads Maven import entries in a contribution manifest.
 *
 * @version $Rev: 5137 $ $Date: 2008-08-02 08:54:51 +0100 (Sat, 02 Aug 2008) $
 */
@EagerInit
public class MavenImportLoader implements TypeLoader<MavenImport> {

    public MavenImport load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);
        String groupId = reader.getAttributeValue(null, "groupId");
        if (groupId == null) {
            MissingMainifestAttribute failure = new MissingMainifestAttribute("The groupId attribute must be specified", "groupId", reader);
            context.addError(failure);
        }
        String artifactId = reader.getAttributeValue(null, "artifactId");
        if (artifactId == null) {
            MissingMainifestAttribute failure = new MissingMainifestAttribute("The artifactId attribute must be specified", "artifactId", reader);
            context.addError(failure);
        }
        String version = reader.getAttributeValue(null, "version");
        String classifier = reader.getAttributeValue(null, "classifier");

        MavenImport imprt = new MavenImport();
        imprt.setGroupId(groupId);
        imprt.setArtifactId(artifactId);
        if (version != null) {
            imprt.setVersion(version);
        }
        if (classifier != null) {
            imprt.setClassifier(classifier);
        }
        return imprt;
    }

    private void validateAttributes(XMLStreamReader reader, IntrospectionContext context) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String name = reader.getAttributeLocalName(i);
            if (!"groupId".equals(name) && !"artifactId".equals(name) && !"version".equals(name) && !"classifier".equals(name)) {
                context.addError(new UnrecognizedAttribute(name, reader));
            }
        }
    }

}
