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

import com.sun.jersey.api.core.DefaultResourceConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @version $Rev: 5443 $ $Date: 2008-09-19 15:55:03 +0100 (Fri, 19 Sep 2008) $
 */
public class SCA4JResourceConfig extends DefaultResourceConfig {

    private SCA4JComponentProviderFactory providerFactory;

    public SCA4JResourceConfig(Map<?, ?> props) {
    }

    public void setProviderFactory(SCA4JComponentProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    @Override
    public Set<Class<?>> getClasses() {
        if (providerFactory == null) {
            return Collections.emptySet();
        }
        return providerFactory.getClasses();
    }

}
