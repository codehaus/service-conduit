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
package org.sca4j.jpa.service;

import java.util.List;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.jpa.model.Employee;
import org.sca4j.jpa.model.ExEmployee;

/**
 * @version $Revision$ $Date$
 */
public class EmployeeServiceImplTest extends TestCase {
    @Reference
    protected EmployeeService employeeService;

    @Reference
    protected EmployeeService employeeMultiThreadedService;

    @Reference
    protected EmployeeService employeeEMFService;

    @Reference
    protected ConversationEmployeeService conversationEmployeeService;

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
