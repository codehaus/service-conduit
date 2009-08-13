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
