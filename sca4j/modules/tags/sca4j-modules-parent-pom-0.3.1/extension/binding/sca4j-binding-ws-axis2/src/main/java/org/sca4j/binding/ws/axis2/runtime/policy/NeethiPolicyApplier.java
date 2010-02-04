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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.binding.ws.axis2.runtime.policy;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.dom.factory.OMDOMFactory;
import org.apache.axis2.description.AxisDescription;
import org.apache.neethi.PolicyEngine;
import org.apache.neethi.builders.AssertionBuilder;
import org.apache.neethi.builders.xml.XMLPrimitiveAssertionBuilder;
import org.osoa.sca.annotations.EagerInit;
import org.w3c.dom.Element;

/**
 * Applies policies based on WS-Policy.
 * 
 * @version $Revision$ $Date$
 */
@EagerInit
public class NeethiPolicyApplier implements PolicyApplier {

    static {
        buildAssertionBuilders();
    }
    
    public NeethiPolicyApplier() {        
    }

    public void applyPolicy(AxisDescription axisDescription, Element policy) {

        try {
        	OMElement policyElement = 
        		(OMElement) new OMDOMFactory().getDocument().importNode(policy, true);

            axisDescription.applyPolicy(PolicyEngine.getPolicy(policyElement));

        } catch (Exception e) {
            // TODO Handle exception properly
            throw new AssertionError(e);
        }

    }

    /*
     * Load assertion builders associated with WS-SP.This is normally done when AssertionBuilderFactory is loaded 
     * but since the Thread Context class loader at that time is the Boot Class loader, so it is not able to find 
     * extensions jars and hence this is done here!!
     * 
     * @see org.apache.neethi.AssertionBuilderFactory
     */
    private static void buildAssertionBuilders() {
        
        // Get the current context class loader
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();

        try {
            
            Thread.currentThread().setContextClassLoader(NeethiPolicyApplier.class.getClassLoader());
            QName XML_ASSERTION_BUILDER = new QName("http://test.org/test", "test");
            
            Iterator<AssertionBuilder> asseryionBuilders = ServiceRegistry.lookupProviders(AssertionBuilder.class);
            while (asseryionBuilders.hasNext()) {
                AssertionBuilder builder = asseryionBuilders.next();
                for (QName knownElement : builder.getKnownElements()) {
                    PolicyEngine.registerBuilder(knownElement, builder);
                }
            }
            
            PolicyEngine.registerBuilder(XML_ASSERTION_BUILDER, new XMLPrimitiveAssertionBuilder());
            
        } finally {
            // Change class loader back to what it was !!
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
}
