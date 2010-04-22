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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.scdl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Base class representing service contract information
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public abstract class ServiceContract extends ModelObject {
    
    protected boolean conversational;
    protected boolean remotable;
    protected String interfaceName;
    protected List<Operation> operations;
    protected ServiceContract callbackContract;
    protected QName portTypeName;

    protected ServiceContract() {
    }
    
    /**
     * Gets the WSDL port type name if one is present.
     * 
     * @return WSDL port type name.
     */
    public QName getPortTypeName() {
        return portTypeName;
    }
    
    /**
     * Sets the WSDL port type name if one is present.
     * 
     * @param portTypeName WSDL Port type name.
     */
    public void setPortTypeName(QName portTypeName) {
        this.portTypeName = portTypeName;
    }

    /**
     * Returns the interface name for the contract
     *
     * @return the interface name for the contract
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Sets the interface name for the contract
     *
     * @param interfaceName the interface name
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * Returns true if the service contract is conversational
     *
     * @return true if the service contract is conversational
     */
    public boolean isConversational() {
        return conversational;
    }

    /**
     * Sets if the service contract is conversational
     *
     * @param conversational the conversational attribute
     */
    public void setConversational(boolean conversational) {
        this.conversational = conversational;
    }

    /**
     * Returns true if the contract is remotable.
     *
     * @return the true if the contract is remotable
     */
    public boolean isRemotable() {
        return remotable;
    }

    /**
     * Sets if the contract is remotable
     *
     * @param remotable true if the contract is remotable
     */
    public void setRemotable(boolean remotable) {
        this.remotable = remotable;
    }

    /**
     * Returns the operations for the service contract.
     *
     * @return the operations for the service contract
     */
    public List<Operation> getOperations() {
        if (operations == null) {
            return Collections.emptyList();
        }
        return operations;
    }

    /**
     * Sets the operations for the service contract.
     *
     * @param operations the operations for the service contract
     */
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    /**
     * Returns the callback contract associated with the service contract or null if the service does not have a callback.
     *
     * @return the callback contract or null
     */
    public ServiceContract getCallbackContract() {
        return callbackContract;
    }

    /**
     * Sets the callback contract associated with the service contract.
     *
     * @param callbackContract the callback contract
     */
    public void setCallbackContract(ServiceContract callbackContract) {
        this.callbackContract = callbackContract;
    }

    /**
     * Determines if this contract is compatible with the given contract. Compatibility is determined according to the specifics of the IDL's
     * compatibility semantics.
     *
     * @param contract the contract to test compatibility with
     * @return true if the contracts are compatible
     */
    public boolean isAssignableFrom(ServiceContract contract) {
        
        //compare contract operations
        List<Operation> theirOperations = contract.getOperations();
        Map<String, Operation> theirOperationNames = new HashMap<String, Operation>();
        
        for (Operation o : theirOperations) {
            if (o.getWsdlName() == null) {
                return false;
            }
            theirOperationNames.put(o.getWsdlName(), o);
        }
        
        for (Operation o : getOperations()) {
            
            if (o.getWsdlName() == null) {
                return false;
            }
            
            Operation theirs = theirOperationNames.remove(o.getWsdlName());
            if (theirs == null) {
                return false;
            }
            
            List<DataType> myParams = o.getInputTypes();
            List<DataType> theirParams = theirs.getInputTypes();

            if (myParams.size() == theirParams.size()) {
                for (int i = 0; i < myParams.size(); i++) {
                    if (!compareTypes(myParams.get(i), theirParams.get(i))) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            if (!compareTypes(o.getOutputType(), theirs.getOutputType())) {
                return false;
            }
            
        }
        return true;
    }
    
    public String toString() {
        return getClass() + " {" + getQualifiedInterfaceName() + "}";
    }

    public abstract String getQualifiedInterfaceName();

    private boolean compareTypes(DataType mine, DataType theirs) {
        QName myType = mine.getXsdType();
        QName theirType = theirs.getXsdType();
        return myType != null && theirType != null && myType.equals(theirType);
    }

    public Operation mapOperation(Operation sourceOperation, ServiceContract sourceServiceContract) {
        
        if (sourceOperation.getWsdlName() == null) {
            return null;
        }
        
        for (Operation o : getOperations()) {
            
            if (o.getWsdlName() == null || !o.getWsdlName().equals(sourceOperation.getWsdlName())) {
                return null;
            }
            
            List<DataType> myParams = o.getInputTypes();
            List<DataType> theirParams = sourceOperation.getInputTypes();

            if (myParams.size() == theirParams.size()) {
                for (int i = 0; i < myParams.size(); i++) {
                    if (!compareTypes(myParams.get(i), theirParams.get(i))) {
                        return null;
                    }
                }
            } else {
                return null;
            }

            if (!compareTypes(o.getOutputType(), sourceOperation.getOutputType())) {
                return null;
            }
            
            return o;
            
        }
        
        return null;
        
    }
    
}
