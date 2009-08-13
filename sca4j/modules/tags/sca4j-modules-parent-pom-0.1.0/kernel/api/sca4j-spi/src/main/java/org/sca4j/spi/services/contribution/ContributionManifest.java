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

package org.sca4j.spi.services.contribution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sca4j.host.contribution.Deployable;

/**
 * Represents a contribution manifest
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
@SuppressWarnings({"SerializableHasSerializationMethods"})
public class ContributionManifest implements Serializable {
    private static final long serialVersionUID = -4968254313720890686L;
    private List<Export> exports = new ArrayList<Export>();
    private List<Import> imports = new ArrayList<Import>();
    private List<Deployable> deployables = new ArrayList<Deployable>();

    public List<Export> getExports() {
        return exports;
    }

    public void addExport(Export export) {
        exports.add(export);
    }

    public List<Import> getImports() {
        return imports;
    }

    public void addImport(Import artifactImport) {
        imports.add(artifactImport);
    }

    public List<Deployable> getDeployables() {
        return deployables;
    }

    public void addDeployable(Deployable deployale) {
        deployables.add(deployale);
    }

}
