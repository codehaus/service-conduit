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

import java.util.Set;
import javax.xml.namespace.QName;

/**
 * Interface that indicates that a SCA definition supports references to intent or policySet definitions.
 *
 * @version $Rev: 921 $ $Date: 2007-08-30 18:53:04 +0100 (Thu, 30 Aug 2007) $
 */
public interface PolicyAware {
    /**
     * Returns the intents this definition references.
     *
     * @return the intents this definition references
     */
    Set<QName> getIntents();

    /**
     * Returns the policySets this definition references.
     *
     * @return the policySets this definition references
     */
    Set<QName> getPolicySets();

    /**
     * Sets the intents this definition references.
     *
     * @param intents the intents this definition references
     */
    void setIntents(Set<QName> intents);

    /**
     * Returns the policySets this definition references.
     *
     * @param policySets the policySets this definition references
     */
    void setPolicySets(Set<QName> policySets);
}
