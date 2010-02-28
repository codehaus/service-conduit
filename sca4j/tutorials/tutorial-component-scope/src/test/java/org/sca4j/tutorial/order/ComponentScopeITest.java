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
 */
package org.sca4j.tutorial.order;

import org.osoa.sca.annotations.Reference;

import junit.framework.TestCase;

public class ComponentScopeITest extends TestCase {
    
    @Reference protected OrderService compositeOrderService;
    @Reference protected OrderService statelessOrderService;
    @Reference protected OrderService requestOrderService;
    @Reference protected ConversationalOrderService conversationalOrderService;
    
    public void testStateless() {
        statelessOrderService.addOrder("pizza");
        assertTrue(statelessOrderService.getOrders().isEmpty());
    }
    
    public void testComposite1() {
        compositeOrderService.addOrder("pizza");
        compositeOrderService.addOrder("coke");
        assertEquals(2, compositeOrderService.getOrders().size());
    }
    
    public void testComposite2() {
        compositeOrderService.addOrder("pizza");
        compositeOrderService.addOrder("coke");
        assertEquals(4, compositeOrderService.getOrders().size());
    }
    
    public void testRequest1() {
        requestOrderService.addOrder("pizza");
        requestOrderService.addOrder("coke");
        assertEquals(2, requestOrderService.getOrders().size());
    }
    
    public void testRequest2() {
        requestOrderService.addOrder("pizza");
        requestOrderService.addOrder("coke");
        assertEquals(2, requestOrderService.getOrders().size());
    }
    
    public void testConversational() {
        conversationalOrderService.addOrder("pizza");
        conversationalOrderService.addOrder("coke");
        assertEquals(2, conversationalOrderService.getOrders().size());
        conversationalOrderService.close();
        conversationalOrderService.addOrder("coke");
        assertEquals(1, conversationalOrderService.getOrders().size());
    }

}
