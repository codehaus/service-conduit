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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Resource;
import javax.sql.DataSource;

public class OrderComponent {
    
    @Resource protected DataSource orderDs;
    
    public String placeOrder(String productName, String address, String creditCard) {
        
        try {
            
            Connection con = orderDs.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("insert into t_order (id, productName, address, creditCard) values (?, ?, ?, ?)");
            
            String orderId = UUID.randomUUID().toString();
            ps.setString(1, orderId);
            ps.setString(2, productName);
            ps.setString(3, address);
            ps.setString(4, creditCard);
            
            ps.executeUpdate();
            con.commit();
            ps.close();
            con.close();
            
            return orderId;
            
        } catch (SQLException e) {
            throw new AssertionError(e);
        }
        
    }

}
