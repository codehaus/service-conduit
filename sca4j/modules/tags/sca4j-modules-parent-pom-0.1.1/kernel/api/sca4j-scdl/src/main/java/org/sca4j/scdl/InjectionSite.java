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

/**
 * Represents an injection site on a Java-based component implementation.
 *
 * @version $Revision: 5233 $ $Date: 2008-08-19 22:50:45 +0100 (Tue, 19 Aug 2008) $
 */
public class InjectionSite extends ModelObject {
    private static final long serialVersionUID = 7792895640425046691L;

    // Name of type being injected
    private String type;

    protected InjectionSite(String type) {
        this.type = type;
    }

    /**
     * Returns the type being injected.
     *
     * @return the name of the type being injected
     */
    public String getType() {
        return type;
    }
}
