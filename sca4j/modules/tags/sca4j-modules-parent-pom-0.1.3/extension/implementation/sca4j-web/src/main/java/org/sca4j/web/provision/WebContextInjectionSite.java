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
package org.sca4j.web.provision;

import org.sca4j.scdl.InjectionSite;

/**
 * An injection site specialized for web applications.
 *
 * @version $Revision$ $Date$
 */
public class WebContextInjectionSite extends InjectionSite {
    private static final long serialVersionUID = 8530588154179239645L;
    private ContextType contextType;

    public static enum ContextType {
        SERVLET_CONTEXT,
        SESSION_CONTEXT
    }

    public WebContextInjectionSite(String type, ContextType contextType) {
        super(type);
        this.contextType = contextType;
    }

    public ContextType getContextType() {
        return contextType;
    }


}
