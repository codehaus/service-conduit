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

import junit.framework.TestCase;

import org.oasisopen.sca.annotation.Reference;

public class OrderComponentITest extends TestCase {
    
    @Reference protected OrderService orderService;
    
    public void testOrder() throws InterruptedException {
        
        orderService.placeOrder("pizza");
        while (!orderService.getStatus()) {
            Thread.sleep(1000);
        }
        orderService.close();
        
    }

}
