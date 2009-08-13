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

package org.sca4j.host.contribution;

import java.io.Serializable;
import javax.xml.namespace.QName;

/**
 * Represents a deployable artifact in a contribution
 *
 * @version $Rev: 556 $ $Date: 2007-07-23 04:58:20 +0100 (Mon, 23 Jul 2007) $
 */
@SuppressWarnings({"SerializableHasSerializationMethods"})
public class Deployable implements Serializable {
    private static final long serialVersionUID = -710863113841788110L;
    private final QName name;
    private final QName type;

    public Deployable(QName name, QName type) {
        this.name = name;
        this.type = type;
    }

    /**
     * The QName of the deployable component.
     *
     * @return QName of the deployable component
     */
    public QName getName() {
        return name;
    }

    /**
     * Returns the deployable type.
     *
     * @return the deployable type
     */
    public QName getType() {
        return type;
    }


}
