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
package org.sca4j.jpa.hibernate;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public final class SCA4JHibernateTransactionManagerLookup implements TransactionManagerLookup {
    
    private static TransactionManager transactionManager;
    
    @Reference
    public void setTransactionManager(TransactionManager transactionManager) {
        SCA4JHibernateTransactionManagerLookup.transactionManager = transactionManager;
    }
    
    public TransactionManager getTransactionManager(Properties properties) throws HibernateException {
        return transactionManager;
    }

    public String getUserTransactionName() {
        return null;
    }

}
