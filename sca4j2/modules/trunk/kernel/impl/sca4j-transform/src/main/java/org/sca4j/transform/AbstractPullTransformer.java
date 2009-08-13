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
package org.sca4j.transform;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Node;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.XSDSimpleType;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
@EagerInit
public abstract class AbstractPullTransformer<SOURCE, TARGET> implements PullTransformer<SOURCE, TARGET> {
	
	/** Default source to be used */
	private static final XSDSimpleType DEFAULT_SOURCE = new XSDSimpleType(Node.class, XSDSimpleType.STRING);
	
    /** Transform Registry to be used*/
    private TransformerRegistry<PullTransformer<SOURCE, TARGET>> registry;

    /**
     * Set Registry
     * @param registry
     */
    @Reference
    public void setRegistry(TransformerRegistry<PullTransformer<SOURCE, TARGET>> registry) {
        this.registry = registry;
    }

    /** Register Transformer*/
    @Init
    public void init() {
        registry.register(this);
    }

    /** Unregister Registry*/
    @Destroy
    public void destroy() {
        registry.unregister(this);
    }
    
    /**
     * @see Transformer#getSourceType()
     */
    public DataType<?> getSourceType() {
    	return DEFAULT_SOURCE;
    }
}
