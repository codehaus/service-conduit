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

import java.net.URI;

import org.sca4j.scdl.validation.MissingPromotion;

/**
 * Representation of a service exposed by a composite component type.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class CompositeService extends ServiceDefinition {
    private static final long serialVersionUID = 7831894579780963064L;
    private URI promote;

    /**
     * Create a composite service definition.
     *
     * @param name            the name to assign to the service
     * @param serviceContract the service contract to expose
     * @param promote         the component service that is being promoted
     */
    public CompositeService(String name, ServiceContract<?> serviceContract, URI promote) {
        super(name, serviceContract);
        this.promote = promote;
    }

    /**
     * Returns the URI of the component service that is being promoted.
     *
     * @return the URI of the component service that is being promoted
     */
    public URI getPromote() {
        return promote;
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);
        if (promote == null) {
            context.addError(new MissingPromotion(this));
        }
    }
}
