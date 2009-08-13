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
package org.sca4j.scdl;

import java.util.List;
import javax.xml.namespace.QName;

/**
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class ConstrainingType extends AbstractComponentType<ServiceDefinition, ReferenceDefinition, Property, ResourceDefinition> {
    private static final long serialVersionUID = 4415016987970558995L;
    private final QName name;
    private final List<QName> requires;

    /**
     * Constructor defining the constraining type name.
     *
     * @param name the qualified name of this constraining type
     * @param requires list of required intents
     */
    public ConstrainingType(QName name, List<QName> requires) {
        this.name = name;
        this.requires = requires;
    }

    /**
     * Returns the qualified name of this constraining type.
     * <p/>
     * The namespace portion of this name is the targetNamespace for other qualified names.
     *
     * @return the qualified name of this constraining type
     */
    public QName getName() {
        return name;
    }

    /**
     * Returns the intents that must be satisfied.
     *
     * @return a list of intents that must be satisified
     */
    public List<QName> getRequires() {
        return requires;
    }
}
