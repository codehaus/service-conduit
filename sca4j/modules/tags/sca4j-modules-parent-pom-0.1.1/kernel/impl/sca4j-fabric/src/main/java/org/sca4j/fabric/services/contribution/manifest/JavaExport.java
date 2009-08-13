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

import org.sca4j.host.Namespaces;
import org.sca4j.spi.services.contribution.Export;
import org.sca4j.spi.services.contribution.Import;

/**
 * Represents an <code>export.java</code> entry in a contribution manifest.
 *
 * @version $Rev: 4825 $ $Date: 2008-06-13 23:21:32 +0100 (Fri, 13 Jun 2008) $
 */
public class JavaExport implements Export {
    private static final long serialVersionUID = -1362112844218693711L;
    private static final QName TYPE = new QName(Namespaces.SCA4J_NS, "java");
    private String packageName;

    public JavaExport(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public int match(Import contributionImport) {
        if (contributionImport instanceof JavaImport
                && ((JavaImport) contributionImport).getPackageName().startsWith(packageName)) {
            return EXACT_MATCH;
        }
        return NO_MATCH;
    }

    public QName getType() {
        return TYPE;
    }
}

