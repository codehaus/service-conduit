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
package org.sca4j.binding.jms.runtime.host.standalone;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.host.work.DefaultPausableWork;

/**
 * A thread pull message from destination and invoke Message listener.
 * 
 * @version $Revision$ $Date$
 */
public class ConsumerWorker extends DefaultPausableWork {

    private ConsumerWorkerTemplate template;
    private boolean exception;

    /**
     * @param template
     */
    public ConsumerWorker(ConsumerWorkerTemplate template) {
        super(true);
        this.template = template;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void execute() {
        
        Connection requestConnection = null;
        Session requestSession = null;
        Connection responseConnection = null;
        Session responseSession = null;
        Destination responseDestination = null;

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        
        try {

            Thread.currentThread().setContextClassLoader(template.cl);
            
            if (exception) {
                exception = false;
                Thread.sleep(template.exceptionTimeout);
            }
            
            requestConnection =  template.requestFactory.getConnection();
            requestSession =  template.requestFactory.getSession(requestConnection, template.transactionType);
            requestConnection.start();
            
            if (template.transactionType == TransactionType.GLOBAL) {
                template.transactionHandler.enlist(requestSession);
            }
            Message message = requestSession.createConsumer(template.requestFactory.getDestination()).receive(template.pollingInterval);
            
            if (message != null) {
                
                if (template.responseFactory != null) {
                    responseConnection = template.responseFactory.getConnection();
                    responseSession = template.responseFactory.getSession(responseConnection, template.transactionType);
                    if (template.transactionType == TransactionType.GLOBAL) {
                        template.transactionHandler.enlist(responseSession);
                    }
                    responseDestination = template.responseFactory.getDestination();
                }
                template.messageListener.onMessage(message, responseSession, responseDestination);
                
                if (template.transactionType == TransactionType.GLOBAL) {
                    template.transactionHandler.commit();
                } else {
                    requestSession.commit();
                }
                
            }
            
        } catch (Exception ex) {  
            reportException(ex);          
            if (template.transactionType == TransactionType.GLOBAL) {
                template.transactionHandler.rollback();
            } else {
                try {
                    if (requestSession != null) {
                        requestSession.rollback();
                    }
                } catch (JMSException ne) {
                    reportException(ne);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
            JmsHelper.closeQuietly(requestConnection);
            JmsHelper.closeQuietly(responseConnection);
        }

    }

    /**
     * Report an exception.
     */
    private void reportException(Exception e) {
        template.monitor.jmsListenerError(e);
        exception = true;
    }

}
