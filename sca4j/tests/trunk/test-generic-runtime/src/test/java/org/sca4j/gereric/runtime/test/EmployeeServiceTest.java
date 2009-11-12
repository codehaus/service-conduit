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
package org.sca4j.gereric.runtime.test;

import javax.persistence.EntityManager;

import org.sca4j.generic.runtime.test.Employee;
import org.sca4j.generic.runtime.test.EmployeeService;
import org.sca4j.runtime.generic.junit.AbstractScaTest;

public class EmployeeServiceTest extends AbstractScaTest {
    
    public EmployeeServiceTest() {
        super("META-INF/root.composite");
    }

    public void test() throws Exception {
        getTransactionManager().begin();
        EmployeeService employeeService = getServiceProxy("employeeService");
        String id = employeeService.createEmployee("Meeraj Kunnumpurath");
        assertEquals("Meeraj Kunnumpurath", employeeService.findName(id));
        Employee employee = new Employee("Babur Begg");
        EntityManager entityManager = getEntityManager("employee");
        entityManager.persist(employee);
        entityManager.flush();
        assertEquals("Babur Begg", entityManager.find(Employee.class, employee.getId()).getName());
        getTransactionManager().rollback();
    }

}
