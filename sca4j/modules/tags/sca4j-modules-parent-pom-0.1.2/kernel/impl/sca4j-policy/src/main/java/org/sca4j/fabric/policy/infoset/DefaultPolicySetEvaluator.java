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
package org.sca4j.fabric.policy.infoset;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class DefaultPolicySetEvaluator implements PolicySetEvaluator {
    
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    public boolean doesApply(Element target, String appliesToXPath, final String operation) {
        
        try {
            
            XPath xpath = XPATH_FACTORY.newXPath();
            
            xpath.setXPathVariableResolver(new XPathVariableResolver() {
                public Object resolveVariable(QName variable) {
                    if ("Operation".equals(variable.getLocalPart())) {
                        return operation;
                    } else {
                        throw new AssertionError("Unexpected variable name " + variable);
                    }
                }
            });
            
            Boolean ret = (Boolean) xpath.evaluate(appliesToXPath, target, XPathConstants.BOOLEAN);
            
            return ret.booleanValue();
            
        } catch (XPathExpressionException ex) {
            throw new AssertionError(ex);
        }
        
    }

}
