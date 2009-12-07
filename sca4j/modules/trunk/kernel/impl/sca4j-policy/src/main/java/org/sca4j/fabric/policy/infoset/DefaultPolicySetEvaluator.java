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
            
            if (false == ret.booleanValue() && System.getProperty("sca4j.debug") != null) {
                System.err.println("Policy with expression " + appliesToXPath + " doesn't apply to " + target.getNodeName());
            }
            
            return ret.booleanValue();
            
        } catch (XPathExpressionException ex) {
            throw new AssertionError(ex);
        }
        
    }

}
