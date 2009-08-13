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

import java.net.URI;
import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;

/**
 * A QName-based contribution import
 *
 * @version $Rev: 4825 $ $Date: 2008-06-13 23:21:32 +0100 (Fri, 13 Jun 2008) $
 */
@SuppressWarnings({"SerializableHasSerializationMethods"})
public class QNameImport implements Import {
    private static final long serialVersionUID = 7714960525252585065L;
    private static final QName TYPE = new QName(Namespaces.SCA4J_NS, "qname");
    private QName namespace;
    private URI location;

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    public QNameImport(QName namespace) {
        this.namespace = namespace;
    }

    public QName getNamespace() {
        return namespace;
    }

    public QName getType() {
        return TYPE;
    }

    public String toString() {
        return "qname [" + namespace + "]";
    }

}
