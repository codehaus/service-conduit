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
