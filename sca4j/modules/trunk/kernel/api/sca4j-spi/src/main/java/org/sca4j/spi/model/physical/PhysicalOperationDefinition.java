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
package org.sca4j.spi.model.physical;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an operation.
 *
 * @version $Revision: 3715 $ $Date: 2008-04-24 18:54:02 +0100 (Thu, 24 Apr 2008) $
 *          <p/>
 *          TODO Discuss with Jeremy/Jim on how to model MEPs, INOUT parameters, faults etc
 */
public class PhysicalOperationDefinition  {

    private List<String> parameterTypes = new LinkedList<String>();
    private String returnType;
    private String name;
    private boolean callback;
    private boolean endsConversation;

    /**
     * Returns the fully qualified parameter types for this operation.
     *
     * @return Parameter types.
     */
    public List<String> getParameters() {
        return parameterTypes;
    }

    /**
     * Add the fully qualified parameter type to the operation.
     *
     * @param parameter Parameter type to be added.
     */
    public void addParameter(String parameter) {
        parameterTypes.add(parameter);
    }

    /**
     * Gets the fuly qualified return type for this operation.
     *
     * @return Return type for this operation.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the fully qualified return type for this operation.
     *
     * @param returnType Return type for this operation.
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Gets the name of the operation.
     *
     * @return Operation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the operation.
     *
     * @param name Operation name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks whether the operation is a callback.
     *
     * @return True if this is a callback.
     */
    public boolean isCallback() {
        return callback;
    }

    /**
     * Sets whether this is a callback operation or not.
     *
     * @param callback True if this is a callback.
     */
    public void setCallback(boolean callback) {
        this.callback = callback;
    }

    /**
     * Returns true if the operation ends a conversation.
     *
     * @return true if the operation ends a conversation
     */
    public boolean isEndsConversation() {
        return endsConversation;
    }

    /**
     * Sets if the operation ends a conversation.
     *
     * @param endsConversation true if the operation ends a conversation
     */
    public void setEndsConversation(boolean endsConversation) {
        this.endsConversation = endsConversation;
    }
}
