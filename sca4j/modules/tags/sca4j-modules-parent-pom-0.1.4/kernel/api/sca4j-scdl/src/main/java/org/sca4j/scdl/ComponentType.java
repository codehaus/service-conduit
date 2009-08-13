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

import javax.xml.namespace.QName;

/**
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class ComponentType extends AbstractComponentType<ServiceDefinition, ReferenceDefinition, Property, ResourceDefinition> {
    private static final long serialVersionUID = 1431781466875898050L;
    private QName constrainingType;

    /**
     * Returns the name of the constraining type for this component type.
     *
     * @return the name of the constraining type for this component type
     */
    public QName getConstrainingType() {
        return constrainingType;
    }

    /**
     * Sets the name of the constraining type for this component type.
     *
     * @param constrainingType the name of the constraining type for this component type
     */
    public void setConstrainingType(QName constrainingType) {
        this.constrainingType = constrainingType;
    }

}
