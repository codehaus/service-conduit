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
package org.sca4j.tests.function.annotation.scope;

import org.osoa.sca.annotations.Reference;

import junit.framework.TestCase;

public class ScopeTest extends TestCase {
    
    private ConversationalService annotatedConversationalService;
    private ConversationalService conversationalService;
    
    private StatelessService annotatedStatelessService;
    private StatelessService statelessService;    
    
    private CompositeService compositeServiceOne;
    private CompositeService compositeServiceTwo;
    
    private CompositeService annotatedCompositeServiceOne;
    private CompositeService annotatedCompositeServiceTwo;

    
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

    @Reference
    public void setAnnotatedConversationalService(ConversationalService annotatedConversationalService) {
        this.annotatedConversationalService = annotatedConversationalService;
    }

    @Reference
    public void setConversationalService(ConversationalService conversationalService) {
        this.conversationalService = conversationalService;
    }

    @Reference
    public void setAnnotatedStatelessService(StatelessService annotatedStatelessService) {
        this.annotatedStatelessService = annotatedStatelessService;
    }

    @Reference
    public void setStatelessService(StatelessService statelessService) {
        this.statelessService = statelessService;
    }

    @Reference
    public void setCompositeServiceOne(CompositeService compositeServiceOne) {
        this.compositeServiceOne = compositeServiceOne;
    }

    @Reference
    public void setCompositeServiceTwo(CompositeService compositeServiceTwo) {
        this.compositeServiceTwo = compositeServiceTwo;
    }

    @Reference
    public void setAnnotatedCompositeServiceOne(CompositeService annotatedCompositeServiceOne) {
        this.annotatedCompositeServiceOne = annotatedCompositeServiceOne;
    }

    @Reference
    public void setAnnotatedCompositeServiceTwo(CompositeService annotatedCompositeServiceTwo) {
        this.annotatedCompositeServiceTwo = annotatedCompositeServiceTwo;
    }
    
    
    
    
}
