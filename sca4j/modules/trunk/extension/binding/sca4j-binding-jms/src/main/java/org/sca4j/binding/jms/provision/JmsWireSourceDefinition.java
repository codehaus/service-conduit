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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.binding.jms.provision;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;

/**
 * @version $Revision: 5225 $ $Date: 2008-08-19 19:07:45 +0100 (Tue, 19 Aug
 *          2008) $
 */
public class JmsWireSourceDefinition extends PhysicalWireSourceDefinition {
    private JmsBindingMetadata metadata;
    private TransactionType transactionType;
    private Set<String> oneWayOperations;
    private Map<String, PayloadType> payloadTypes;

    /**
     * Constructor
     * 
     * @param uri The service URI
     * @param metadata Metadata to be initialized.
     * @param payloadTypes The JMS payload types keyed by operation name
     * @param transactionType Transaction type
     * @param oneWayOperations The set of oneway operation names
     * @param classloaderId the classloader id associated with user datatypes
     */
    public JmsWireSourceDefinition(URI uri, JmsBindingMetadata metadata, Map<String, PayloadType> payloadTypes, TransactionType transactionType, Set<String> oneWayOperations,
            URI classloaderId) {
        this.metadata = metadata;
        this.transactionType = transactionType;
        this.oneWayOperations = oneWayOperations;
        this.payloadTypes = payloadTypes;
        setUri(uri);
        setClassLoaderId(classloaderId);
    }

    /**
     * @return JMS metadata.
     */
    public JmsBindingMetadata getMetadata() {
        return metadata;
    }

    /**
     * Returns the payload type keyed by operation name
     * 
     * @return the payload type
     */
    public Map<String, PayloadType> getPayloadTypes() {
        return payloadTypes;
    }

    /**
     * @return Transaction type.
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType Transaction type.
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Returns the operation names for the wire.
     * 
     * @return the operation names for the wire
     */
    public Set<String> getOneWayOperations() {
        return oneWayOperations;
    }

}
