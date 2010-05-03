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

import java.util.concurrent.atomic.AtomicBoolean;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
import org.sca4j.api.annotation.scope.Conversation;
import org.sca4j.tutorial.shipping.ShippingCallbackService;
import org.sca4j.tutorial.shipping.ShippingService;

@Conversation
@Service({OrderService.class, ShippingCallbackService.class})
public class OrderComponent implements OrderService, ShippingCallbackService {
    
    @Reference protected ShippingService shippingService;
    
    private AtomicBoolean shipped = new AtomicBoolean(false);

    public boolean getStatus() {
        return shipped.get();
    }

    public void placeOrder(String productName) {
        shippingService.ship(productName);
    }

    public void shipped() {
        shipped.set(true);
    }
    
    public void close() {
    }

}
