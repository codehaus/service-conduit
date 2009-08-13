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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Session;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.Scope;
import org.sca4j.jpa.model.Employee;
import org.sca4j.jpa.model.ExEmployee;

/**
 * @version $Revision$ $Date$
 */
@Scope("CONVERSATION")
public class ConversationEmployeeServiceImpl implements ConversationEmployeeService {
    private EntityManager employeeEM;
    private EntityManager exEmployeeEM;

    @PersistenceContext(name = "employeeEmf", unitName = "employee", type = PersistenceContextType.EXTENDED)
    public void setEmployeeEM(EntityManager em) {
        this.employeeEM = em;
    }

    @PersistenceContext(name = "exEmployeeEmf", unitName = "ex-employee", type = PersistenceContextType.EXTENDED)
    public void setExEmployeeEM(EntityManager em) {
        this.exEmployeeEM = em;
    }

    public Employee createEmployee(Long id, String name) {
    	
    	Employee employee = new Employee(id, name);
        employeeEM.persist(employee);
        return employee;
    }

    public Employee findEmployee(Long id) {
        return employeeEM.find(Employee.class, id);
    }

    public ExEmployee findExEmployee(Long id) {
        return exEmployeeEM.find(ExEmployee.class, id);
    }

    public void removeExEmployee(Long id) {
        ExEmployee exEmployee = exEmployeeEM.find(ExEmployee.class, id);
        exEmployeeEM.remove(exEmployee);
    }

    public void removeEmployee(Long id) {
        Employee employee = employeeEM.find(Employee.class, id);
        employeeEM.remove(employee);
    }


    public Employee updateEmployee(Employee employee) {
        return employeeEM.merge(employee);
    }

    @EndsConversation
    public void end() {
        // no-op
    }

    public List<Employee> searchWithCriteria(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public void fire(Long id) {
        Employee employee = employeeEM.find(Employee.class, id);
        employeeEM.remove(employee);
        ExEmployee exEmployee = new ExEmployee(employee.getId(),employee.getName());
        exEmployeeEM.persist(exEmployee);
    }
}
