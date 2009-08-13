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
package org.sca4j.jpa.hibernate;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.Import;
import org.sca4j.spi.services.contribution.MavenImport;
import org.sca4j.spi.services.contribution.XmlElementManifestProcessor;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;

/**
 * Adds an implicit import of the Hibernate contribution extension into any contribution using JPA on a runtime cnfigured to use Hibernate. This is
 * necessary as Hibernate's use of CGLIB for generating proxies requires that Hibernate classes (specifically, HibernateDelegate) be visible from the
 * classloader that loaded a particular entity (i.e. the application classloader). If a Hibernate is explicitly imported in a contribution manifest
 * (sca-contribution.xml), it is used instead.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class HibernatePersistenceManifestProcessor implements XmlElementManifestProcessor {
    public static final QName PERSISTENCE = new QName("http://java.sun.com/xml/ns/persistence", "persistence");
    public static final String GROUP_ID = "org.sca4j";
    public static final String ARTIFACT_ID = "sca4j-jpa-hibernate";
    private XmlManifestProcessorRegistry registry;

    public HibernatePersistenceManifestProcessor(@Reference XmlManifestProcessorRegistry registry) {
        this.registry = registry;
    }

    public QName getType() {
        return PERSISTENCE;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public void process(ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException {
        // TODO this assumes Hibernate is available on the controller, which is not necessary since it is only required at runtime.
        //  An import scope similar to Maven's "runtime" would be a possible solution.
        for (Import imprt : manifest.getImports()) {
            if (imprt instanceof MavenImport) {
                MavenImport mvnImport = (MavenImport) imprt;
                if (ARTIFACT_ID.equals(mvnImport.getArtifactId()) && GROUP_ID.equals(mvnImport.getGroupId())) {
                    // already explicitly imported, return
                    return;
                }
            }
        }
        MavenImport imprt = new MavenImport();
        imprt.setGroupId(GROUP_ID);
        imprt.setArtifactId(ARTIFACT_ID);
        manifest.addImport(imprt);
    }
}
