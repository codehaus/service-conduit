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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.sca4j.jpa.model.Employee;
import org.sca4j.jpa.model.ExEmployee;

/**
 * Exercises injecting an EntityManagerFactory.
 *
 * @version $Revision$ $Date$
 */
public class EmployeeServiceEMFImpl implements EmployeeService {
    private EntityManagerFactory emf;

    @PersistenceUnit(name = "employeeEmf", unitName = "employee")
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Employee createEmployee(Long id, String name) {
        EntityManager em = emf.createEntityManager();
        Employee employee = new Employee(id, name);
        em.persist(employee);
        em.flush();
        return employee;

    }

    public Employee findEmployee(Long id) {
        return emf.createEntityManager().find(Employee.class, id);
    }

    public void removeEmployee(Long id) {
        EntityManager em = emf.createEntityManager();
        Employee employee = em.find(Employee.class, id);
        em.remove(employee);
        em.flush();
    }

	public List<Employee> searchWithCriteria(String name) {
		return null;
	}

    public void fire(Long id) {
        throw new UnsupportedOperationException();
    }

    public ExEmployee findExEmployee(Long id) {
        return null;
    }

    public void removeExEmployee(Long id) {

    }


}
