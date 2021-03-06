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

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.MavenExport;
import org.sca4j.spi.services.contribution.XmlElementManifestProcessor;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;

/**
 * Loads Maven export entries in a contribution manifest by parsing a pom.xml file contained in a contribution.
 *
 * @version $Rev: 5288 $ $Date: 2008-08-28 08:43:01 +0100 (Thu, 28 Aug 2008) $
 */
@EagerInit
public class MavenPOMProcessor implements XmlElementManifestProcessor {

    public static final String NS = "http://maven.apache.org/POM/4.0.0";
    private static final QName PROJECT = new QName(NS, "project");

    private XmlManifestProcessorRegistry registry;

    public MavenPOMProcessor(@Reference XmlManifestProcessorRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public QName getType() {
        return PROJECT;
    }

    public void process(ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException {
        String parentVersion = null;
        String parentGroupId = null;
        String groupId = null;
        String artifactId = null;
        String version = null;
        String packaging = null;
        boolean loop = true;
        QName firstChildElement = null;
        try {
            while (loop) {
                switch (reader.next()) {
                case START_ELEMENT:
                    if (reader.getName().getLocalPart().equals("parent")) {
                    	String[] ret = parseParent(reader);
                        parentVersion = ret[0];
                        parentGroupId = ret[1];
                    } else if (firstChildElement == null && reader.getName().getLocalPart().equals("groupId")) {
                        groupId = reader.getElementText();
                    } else if (firstChildElement == null && reader.getName().getLocalPart().equals("artifactId")) {
                        artifactId = reader.getElementText();
                    } else if (firstChildElement == null && reader.getName().getLocalPart().equals("packaging")) {
                        packaging = reader.getElementText();
                    } else if (firstChildElement == null && reader.getName().getLocalPart().equals("version")) {
                        version = reader.getElementText();
                    } else if (firstChildElement == null) {
                        // keep track of child element below <project>. This is used to ignore values in subelements for version, groupid, and
                        // artifact id wich pertain to different contexts, e.g. <dependency>
                        firstChildElement = reader.getName();
                    }
                    break;
                case END_ELEMENT:
                    if (reader.getName().getLocalPart().equals("project")) {
                        loop = false;
                        continue;
                    } else {
                        if (reader.getName().equals(firstChildElement)) {
                            firstChildElement = null;
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
        }

        if (version == null || "".equals(version)) {
            version = parentVersion;
        }

        if (groupId == null || "".equals(groupId)) {
        	groupId = parentGroupId;
        }

        if (groupId == null || "".equals(groupId)) {
            context.addError(new InvalidPOM("Group id not specified", "groupId", reader));
        }
        if (artifactId == null || "".equals(artifactId)) {
            context.addError(new InvalidPOM("Artifact id not specified", "artifactId", reader));
        }
        if (version == null || "".equals(version)) {
            context.addError(new InvalidPOM("Version not specified", "version", reader));
        }

        MavenExport export = new MavenExport();
        export.setGroupId(groupId);
        export.setArtifactId(artifactId);
        export.setVersion(version);

        if (packaging != null && "".equals(packaging)) {
            export.setClassifier(packaging);
        }

        manifest.addExport(export);


    }

    private String[] parseParent(XMLStreamReader reader) throws XMLStreamException {
        String ret[] = new String[2];
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                if (reader.getName().getLocalPart().equals("version")) {
                    ret[0] = reader.getElementText();
                } else if (reader.getName().getLocalPart().equals("groupId")) {
                	ret[1] = reader.getElementText();
                }
                break;
            case END_ELEMENT:
                if (reader.getName().getLocalPart().equals("parent")) {
                    return ret;
                }
            }
        }
    }
}
