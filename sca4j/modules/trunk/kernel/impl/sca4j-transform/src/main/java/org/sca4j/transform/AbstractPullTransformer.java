/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
