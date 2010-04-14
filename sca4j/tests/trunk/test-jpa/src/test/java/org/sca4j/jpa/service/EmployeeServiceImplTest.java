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
package org.sca4j.jpa.service;

import java.util.List;

import junit.framework.TestCase;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.jpa.model.Employee;
import org.sca4j.jpa.model.ExEmployee;

/**
 * @version $Revision$ $Date$
 */
public class EmployeeServiceImplTest extends TestCase {
    
    @Reference protected EmployeeService employeeService;
    @Reference protected EmployeeService employeeMultiThreadedService;
    @Reference protected EmployeeService employeeEMFService;
    @Reference protected ConversationEmployeeService conversationEmployeeService;
    @Reference protected EmployeeService employeeHibernateService;

    public void testCreateEmployee() {
        employeeService.createEmployee(123L, "Barney Rubble");
        Employee employee = employeeService.findEmployee(123L);
        assertNotNull(employee);
        assertEquals("Barney Rubble", employee.getName());
    }

    public void testCreateEMFEmployee() throws Exception {
        employeeEMFService.createEmployee(123L, "Barney Rubble");
        Employee employee = employeeEMFService.findEmployee(123L);
        assertNotNull(employee);
        assertEquals("Barney Rubble", employee.getName());
    }

    public void testCreateHibernateEmployee() throws Exception {
        employeeHibernateService.createEmployee(123L, "Barney Rubble");
        Employee employee = employeeHibernateService.findEmployee(123L);
        assertNotNull(employee);
        assertEquals("Barney Rubble", employee.getName());
    }

    public void testCreateMultiThreadedEmployee() {
        employeeMultiThreadedService.createEmployee(123L, "Barney Rubble");
        Employee employee = employeeMultiThreadedService.findEmployee(123L);
        assertNotNull(employee);
        assertEquals("Barney Rubble", employee.getName());
    }

    public void testSearchWithName() {
        employeeMultiThreadedService.createEmployee(123L, "Barney");
        List<Employee> employees = employeeService.searchWithCriteria("Barney");
        assertNotNull(employees);
        assertEquals(1, employees.size());
    }

    public void testTwoPersistenceContexts() {
        employeeService.createEmployee(123L, "Barney Rubble");
        employeeService.fire(123L);
    }

    public void testExtendedPersistenceContext() {
        conversationEmployeeService.createEmployee(123L, "Barney Rubble");
        Employee employee = conversationEmployeeService.findEmployee(123L);
        assertNotNull(employee);
        assertEquals("Barney Rubble", employee.getName());
        // verify the object has not be detached
        employee.setName("Fred Flintstone");
        Employee employee2 = conversationEmployeeService.updateEmployee(employee);
        // the merge operation should use the same persistent entity since it is never detached for extended persistence contexts
        assertSame(employee, employee2);
        employee = conversationEmployeeService.findEmployee(123L);
        assertEquals("Fred Flintstone", employee.getName());
        // end the conversation, which should also close the EntityManager/persistence context
        conversationEmployeeService.end();
        employee2 = conversationEmployeeService.findEmployee(123L);
        // employee2 should be loaded in a different persistence context and not the same as the original
        assertNotSame(employee, employee2);
    }

    public void testTwoExtendedPersistenceContexts() {
        conversationEmployeeService.createEmployee(123L, "Barney Rubble");
        conversationEmployeeService.fire(123L);
        conversationEmployeeService.end();
    }


    protected void setUp() throws Exception {
        super.setUp();
        Employee employee = employeeService.findEmployee(123L);
        if (employee != null) {
            employeeService.removeEmployee(123L);
        }
        ExEmployee exEmployee = employeeService.findExEmployee(123L);
        if (exEmployee != null) {
            employeeService.removeExEmployee(123L);
        }
    }


}
