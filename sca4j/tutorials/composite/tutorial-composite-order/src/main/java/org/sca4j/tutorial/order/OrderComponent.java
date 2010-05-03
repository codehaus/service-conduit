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

import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.tutorial.billing.BillingComponent;
import org.sca4j.tutorial.shipping.ShippingComponent;

public class OrderComponent {
    
    @Reference protected BillingComponent billingComponent;
    @Reference protected ShippingComponent shippingComponent;
    @Property protected int deliveryCharge;
    @Property protected int valueAddedTax;

    public boolean placeOrder(String productName, String address, String creditCard) {
        double price = 2.0 + 2.0 * valueAddedTax / 100 + deliveryCharge;
        if (billingComponent.bill(address, creditCard, price)) {
            shippingComponent.ship(productName, address);
            return true;
        }
        return false;
    }

}
