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
package org.sca4j.rs.scdl;

import java.net.URI;
import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * @version $Rev: 4739 $ $Date: 2008-06-05 19:39:31 +0100 (Thu, 05 Jun 2008) $
 */
public class RsBindingDefinition extends BindingDefinition {

    public static final QName BINDING_RS = new QName(Namespaces.SCA4J_NS, "binding.rs");
    private boolean isResource;
    private boolean isProvider;

    public RsBindingDefinition(URI targetUri, Document key) {
        super(targetUri, BINDING_RS, key);
    }

    public boolean isProvider() {
        return isProvider;
    }

    public void setIsProvider(boolean value) {
        this.isProvider = value;
    }

    public boolean isResource() {
        return isResource;
    }

    public void setIsResource(boolean value) {
        this.isResource = value;
    }
}
