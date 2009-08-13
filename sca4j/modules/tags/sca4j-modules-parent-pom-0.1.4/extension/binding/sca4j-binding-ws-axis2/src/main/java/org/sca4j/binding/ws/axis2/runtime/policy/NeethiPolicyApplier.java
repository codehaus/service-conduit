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
