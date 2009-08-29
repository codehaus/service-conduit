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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
    public static final String ARTIFACT_ID = "sca4j-jpa";
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
