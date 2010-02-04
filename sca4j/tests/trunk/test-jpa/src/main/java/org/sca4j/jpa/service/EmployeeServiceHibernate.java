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

import javax.persistence.PersistenceUnit;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sca4j.jpa.model.Employee;
import org.sca4j.jpa.model.ExEmployee;

/**
 * Exercises injecting an EntityManagerFactory.
 *
 * @version $Revision$ $Date$
 */
public class EmployeeServiceHibernate implements EmployeeService {
    
    @PersistenceUnit(name = "employeeEmf", unitName = "employee") protected SessionFactory sessionFactory;

    public Employee createEmployee(Long id, String name) {
        Session em = sessionFactory.openSession();
        Employee employee = new Employee(id, name);
        em.persist(employee);
        em.flush();
        return employee;
    }

    public Employee findEmployee(Long id) {
        return (Employee) sessionFactory.openSession().load(Employee.class, id);
    }

    public void removeEmployee(Long id) {
        Session em = sessionFactory.openSession();
        Employee employee = (Employee) em.load(Employee.class, id);
        em.delete(employee);
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
