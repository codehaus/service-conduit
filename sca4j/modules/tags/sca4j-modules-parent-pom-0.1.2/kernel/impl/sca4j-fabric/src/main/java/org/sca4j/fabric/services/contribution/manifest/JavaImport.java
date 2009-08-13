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

import java.net.URI;
import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.spi.services.contribution.Import;

/**
 * Represents an <code>import.java</code> entry in a contribution manifest.
 *
 * @version $Rev: 4825 $ $Date: 2008-06-13 23:21:32 +0100 (Fri, 13 Jun 2008) $
 */
public class JavaImport implements Import {
    private static final long serialVersionUID = -7863768515125756048L;
    private static final QName TYPE = new QName(Namespaces.SCA4J_NS, "java");
    private String packageName;
    private URI location;

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    public JavaImport(String namespace) {
        this.packageName = namespace;
    }

    public String getPackageName() {
        return packageName;
    }

    public QName getType() {
        return TYPE;
    }


    public String toString() {
        return "package [" + packageName + "]";
    }
}
