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
package org.sca4j.jpa.provider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import junit.framework.TestCase;

/**
 * @version $Revision: 2755 $ $Date: 2008-02-13 08:02:41 +0000 (Wed, 13 Feb 2008) $
 */
public class ThirdPersistenceUnitTest extends TestCase {
        
	private EntityManagerFactory emf;    

	@PersistenceUnit(unitName="employeeUnitThree")
	public void setEmfOne(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public void testEmfExists() {
		EntityManager em = emf.createEntityManager();    	
		assertNotNull(em);	        
    }
}
