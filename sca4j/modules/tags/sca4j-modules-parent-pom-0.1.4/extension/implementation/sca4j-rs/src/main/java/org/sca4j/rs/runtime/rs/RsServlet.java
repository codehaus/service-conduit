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
package org.sca4j.rs.runtime.rs;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @version $Rev: 4739 $ $Date: 2008-06-05 19:39:31 +0100 (Thu, 05 Jun 2008) $
 */
public class RsServlet extends ServletContainer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private SCA4JComponentProviderFactory componentProviderFactory;

    public RsServlet(SCA4JComponentProviderFactory componentProviderFactory) {
        this.componentProviderFactory = componentProviderFactory;
    }

    @Override
    protected void initiate(ResourceConfig rc, WebApplication wa) {
        if (rc instanceof SCA4JResourceConfig) {
            SCA4JResourceConfig f3rc = (SCA4JResourceConfig) rc;
            f3rc.setProviderFactory(componentProviderFactory);
            wa.initiate(rc, componentProviderFactory);
        } else {
            wa.initiate(rc);
        }

    }
    
}
