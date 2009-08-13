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
package org.sca4j.web.introspection;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Implementation;

/**
 * Represents the implementation of a web artifact such as a servlet or filter.
 *
 * @version $Revision$ $Date$
 */
public class WebArtifactImplementation extends Implementation<PojoComponentType> {
    private static final long serialVersionUID = -5415465119697665067L;
    public static final QName QNAME = new QName(Namespaces.SCA4J_NS, "webArtifact");

    public QName getType() {
        return QNAME;
    }
}
