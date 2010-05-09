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
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.oasisopen.sca.annotation.Reference;

import junit.framework.TestCase;

public class OrderComponentITest extends TestCase {

    @Resource protected DataSource orderDs;
    @Reference protected BeanManagedOrderComponent beanManagedOrderComponent;
    @Reference protected ContainerManagedOrderComponent containerManagedOrderComponent;
    
    public void testBeanManagedOrder() throws SQLException {
        Connection con = orderDs.getConnection();
        Statement stmt = con.createStatement();
        stmt.execute("create table t_order (id varchar(40), productName varchar(10), address varchar(10), creditCard varchar(10))");
        String orderId = beanManagedOrderComponent.placeOrder("Pizza", "ABC", "DEF");
        assertEquals("Pizza", beanManagedOrderComponent.getOrder(orderId));
        stmt.execute("drop table t_order");
        stmt.close();
        con.close();
    }
    
    public void testContainerManagedOrder() throws SQLException {
        Connection con = orderDs.getConnection();
        Statement stmt = con.createStatement();
        stmt.execute("create table t_order (id varchar(40), productName varchar(10), address varchar(10), creditCard varchar(10))");
        String orderId = containerManagedOrderComponent.placeOrder("Pizza", "ABC", "DEF");
        assertEquals("Pizza", containerManagedOrderComponent.getOrder(orderId));
        stmt.execute("drop table t_order");
        stmt.close();
        con.close();
    }
    
}
