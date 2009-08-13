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
package org.sca4j.jpa.runtime;

import javax.persistence.EntityManagerFactory;

import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;

/**
 * Target interceptor for entity manager factory.
 *
 * @version $Revision$ $Date$
 */
public class EmfInterceptor implements Interceptor {

    private Interceptor next;
    private String opName;
    private EntityManagerFactory entityManagerFactory;

    public EmfInterceptor(String opName, EntityManagerFactory entityManagerFactory) {
        this.opName = opName;
        this.entityManagerFactory = entityManagerFactory;
    }

    public Interceptor getNext() {
        return next;
    }

    public Message invoke(Message msg) {

        Object ret = null;

        // TODO cater for the overloaded createEntityManager method
        if ("createEntityManager".equals(opName)) {
            ret = entityManagerFactory.createEntityManager();
        } else if ("close".equals(opName)) {
            entityManagerFactory.close();
        } else if ("isOpen".equals(opName)) {
            ret = entityManagerFactory.isOpen();
        }

        Message result = new MessageImpl();
        result.setBody(ret);

        return result;

    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

}

