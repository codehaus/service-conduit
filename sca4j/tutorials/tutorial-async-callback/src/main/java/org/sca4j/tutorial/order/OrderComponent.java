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

import java.util.LinkedList;
import java.util.List;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.scope.Conversation;
import org.sca4j.tutorial.shipping.ShippingCallbackService;
import org.sca4j.tutorial.shipping.ShippingService;

@Conversation
@Service(interfaces = {OrderService.class, ShippingCallbackService.class})
public class OrderComponent implements OrderService, ShippingCallbackService {
    
    @Reference protected ShippingService shippingService;
    
    private List<String> shippedOrders = new LinkedList<String>();

    public boolean getStatus(String orderId) {
        return shippedOrders.contains(orderId);
    }

    public void placeOrder(String orderId) {
        shippingService.ship(orderId);
    }

    public void shipped(String orderId) {
        shippedOrders.add(orderId);
    }
    
    public void close() {
    }

}
