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
import java.util.List;

import org.sca4j.scdl.validation.MissingPromotion;

/**
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class CompositeReference extends ReferenceDefinition {
    private static final long serialVersionUID = 5387987439912912994L;

    private final List<URI> promotedUris;

    /**
     * Construct a composite reference.
     *
     * @param name         the name of the composite reference
     * @param promotedUris the list of component references it promotes
     */
    public CompositeReference(String name, List<URI> promotedUris) {
        super(name, null);
        this.promotedUris = promotedUris;
    }

    /**
     * Returns the list of references this composite reference promotes.
     *
     * @return the list of references this composite reference promotes
     */
    public List<URI> getPromotedUris() {
        return promotedUris;
    }

    /**
     * Adds the URI of a reference this composite reference promotes.
     *
     * @param uri the promoted reference URI
     */
    public void addPromotedUri(URI uri) {
        promotedUris.add(uri);
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);
        if (promotedUris == null || promotedUris.isEmpty()) {
            context.addError(new MissingPromotion(this));
        }
    }
}
