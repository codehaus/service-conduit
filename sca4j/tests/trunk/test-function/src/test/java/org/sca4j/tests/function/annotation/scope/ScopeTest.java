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
package org.sca4j.tests.function.annotation.scope;

import org.oasisopen.sca.annotation.Reference;

import junit.framework.TestCase;

public class ScopeTest extends TestCase {
    
    @Reference protected ConversationalService annotatedConversationalService;
    @Reference protected ConversationalService conversationalService;
    
    @Reference protected StatelessService annotatedStatelessService;
    @Reference protected StatelessService statelessService;    
    
    @Reference protected CompositeService compositeServiceOne;
    @Reference protected CompositeService compositeServiceTwo;
    
    @Reference protected CompositeService annotatedCompositeServiceOne;
    @Reference protected CompositeService annotatedCompositeServiceTwo;
    
    @Reference protected RequestService requestService;

    
    public void testAnnotatedCompositeScope() throws Exception {
        assertEquals("Unexpected initial value", 0, annotatedCompositeServiceOne.getValue());
        assertEquals("Unexpected initial value", 0, annotatedCompositeServiceTwo.getValue());
        
        annotatedCompositeServiceOne.incrementValue();
        assertEquals("Unexpected value", 1, annotatedCompositeServiceOne.getValue());
        assertEquals("Unexpected value", 1, annotatedCompositeServiceTwo.getValue());        
        
        annotatedCompositeServiceOne.incrementValue();
        assertEquals("Unexpected value", 2, annotatedCompositeServiceOne.getValue());          
        assertEquals("Unexpected value", 2, annotatedCompositeServiceTwo.getValue());        
    }
    
    public void testCompositeScope() throws Exception {
        assertEquals("Unexpected initial value", 0, compositeServiceOne.getValue());
        assertEquals("Unexpected initial value", 0, compositeServiceTwo.getValue());
        
        compositeServiceOne.incrementValue();
        assertEquals("Unexpected value", 1, compositeServiceOne.getValue());
        assertEquals("Unexpected value", 1, compositeServiceTwo.getValue());        
        
        compositeServiceOne.incrementValue();
        assertEquals("Unexpected value", 2, compositeServiceOne.getValue());          
        assertEquals("Unexpected value", 2, compositeServiceTwo.getValue());        
    }    
    
    public void testAnnotatedStatelessScope() throws Exception {
        assertEquals("Unexpected initial value", 0, annotatedStatelessService.getValue());
        
        annotatedStatelessService.incrementValue();
        assertEquals("Unexpected value", 0, annotatedStatelessService.getValue());
        
        annotatedStatelessService.incrementValue();
        assertEquals("Unexpected value", 0, annotatedStatelessService.getValue());        
    }
    
    public void testStatelessScope() throws Exception {
        assertEquals("Unexpected initial value", 0, statelessService.getValue());
        
        statelessService.incrementValue();
        assertEquals("Unexpected value", 0, statelessService.getValue());
        
        statelessService.incrementValue();
        assertEquals("Unexpected value", 0, statelessService.getValue());        
    }            
    
    public void testAnnotatedConversationalScope() throws Exception {
        assertEquals("Unexpected initial value", 0, annotatedConversationalService.getValue());
        
        annotatedConversationalService.incrementValue();
        assertEquals("Unexpected value", 1, annotatedConversationalService.getValue());
        
        annotatedConversationalService.incrementValue();
        assertEquals("Unexpected value", 2, annotatedConversationalService.getValue());
    }
    
    public void testConversationalScope() throws Exception {
        assertEquals("Unexpected initial value", 0, conversationalService.getValue());
        
        conversationalService.incrementValue();
        assertEquals("Unexpected value", 1, conversationalService.getValue());
        
        conversationalService.incrementValue();
        assertEquals("Unexpected value", 2, conversationalService.getValue());        
    }
    
    public void testRequestScope() throws Exception {
        assertEquals("Unexpected initial value", 0, requestService.getValue());
        requestService.incrementValue();
        assertEquals("Unexpected value", 1, requestService.getValue());
        requestService.incrementValue();
        assertEquals("Unexpected value", 2, requestService.getValue());        
    }
    
}
