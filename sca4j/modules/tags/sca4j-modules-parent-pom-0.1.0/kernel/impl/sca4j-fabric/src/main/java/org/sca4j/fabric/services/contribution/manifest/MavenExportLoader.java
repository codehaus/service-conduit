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
package org.sca4j.fabric.services.contribution.manifest;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;
import org.sca4j.introspection.xml.UnrecognizedAttribute;
import org.sca4j.spi.services.contribution.MavenExport;

/**
 * Loads Maven export entries in a contribution manifest.
 *
 * @version $Rev: 5137 $ $Date: 2008-08-02 08:54:51 +0100 (Sat, 02 Aug 2008) $
 */
@EagerInit
public class MavenExportLoader implements TypeLoader<MavenExport> {

    public MavenExport load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        validateAttributes(reader, context);
        String groupId = reader.getAttributeValue(null, "groupId");
        if (groupId == null) {
            MissingMainifestAttribute failure = new MissingMainifestAttribute("The groupId attribute must be specified", "groupId", reader);
            context.addError(failure);
            return null;
        }
        String artifactId = reader.getAttributeValue(null, "artifactId");
        if (artifactId == null) {
            MissingMainifestAttribute failure = new MissingMainifestAttribute("The artifictId attribute must be specified", "artifictId", reader);
            context.addError(failure);
            return null;
        }
        String version = reader.getAttributeValue(null, "version");
        String classifier = reader.getAttributeValue(null, "classifier");

        MavenExport export = new MavenExport();
        export.setGroupId(groupId);
        export.setArtifactId(artifactId);
        if (version != null) {
            export.setVersion(version);
        }
        if (classifier != null) {
            export.setClassifier(classifier);
        }
        return export;
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
