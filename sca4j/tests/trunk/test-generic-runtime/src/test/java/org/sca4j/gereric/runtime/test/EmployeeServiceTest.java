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

import java.net.URI;

import javax.persistence.EntityManager;
import javax.xml.namespace.QName;

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
        Employee employee = new Employee("Meeraj Kunnumpurath"); 
        employeeService.createEmployee(employee);
        assertEquals("Meeraj Kunnumpurath", employeeService.findName(employee).getName());
        
        employee = new Employee("Babur Begg");
        EntityManager entityManager = getEntityManager("employee");
        entityManager.persist(employee);
        entityManager.flush();
        assertEquals("Babur Begg", entityManager.find(Employee.class, employee.getId()).getName());
        
        getTransactionManager().rollback();
        
        QName bindingType = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "binding.ws");
        URI endpointUri = URI.create("http://localhost:8080/axis2/employee");
        employeeService = getBinding(EmployeeService.class, bindingType, endpointUri);
        employee = new Employee("Neil Ellis");
        employeeService.createEmployee(employee);
        assertEquals("Neil Ellis", employeeService.findName(employee).getName());
        
    }

}
