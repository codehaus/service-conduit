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
package org.sca4j.runtime.webapp.smoketest;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import org.sca4j.runtime.webapp.smoketest.model.Employee;

/**
 * @version $Revision$ $Date$
 */
public class EmployeeServiceImpl implements EmployeeService {
    private EntityManager em;

    @PersistenceContext(name = "employeeEmf", unitName = "employee")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Employee createEmployee(Long id, String name) {
        Employee employee = new Employee(id, name);
        em.persist(employee);
        return employee;
    }

    public Employee findEmployee(Long id) {
        Employee e = em.find(Employee.class, id);
        return e;
    }

    public void removeEmployee(Long id) {
        Employee employee = em.find(Employee.class, id);
        em.remove(employee);
    }

    public List<Employee> searchWithCriteria(String name) {
        Session session = (Session) em.getDelegate();
        Criteria criteria = session.createCriteria(Employee.class);

        criteria.add(Restrictions.eq("name", name));

        return (List<Employee>) criteria.list();
    }

}
