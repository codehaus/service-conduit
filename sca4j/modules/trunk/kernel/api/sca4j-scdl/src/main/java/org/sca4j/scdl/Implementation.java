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

import javax.xml.namespace.QName;

import org.sca4j.scdl.validation.MissingComponentType;

/**
 * Represents a component implementation
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public abstract class Implementation<T extends AbstractComponentType<?, ?, ?, ?>> extends AbstractPolicyAware {
    private static final long serialVersionUID = -6060603636927660850L;
    private T componentType;

    protected Implementation() {
    }

    protected Implementation(T componentType) {
        this.componentType = componentType;
    }

    public T getComponentType() {
        return componentType;
    }

    public void setComponentType(T componentType) {
        this.componentType = componentType;
    }

    /**
     * Returns true if this implementation corresponds to the supplied XML element.
     *
     * @param type the QName of the implementation element
     * @return true if this instance is of the supplied type
     */
    public boolean isType(QName type) {
        return getType().equals(type);
    }

    /**
     * Returns true if this implementation is a composite.
     * <p/>
     * This indicates whether this implementation can have children in the logical assembly.
     *
     * @return true if this implementation is a composite
     */
    public boolean isComposite() {
        return false;
    }

    /**
     * Returns the SCDL XML element corresponding to this type.
     *
     * @return the SCDL XML element corresponding to this type
     */
    public abstract QName getType();

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);
        if (componentType == null) {
            context.addError(new MissingComponentType(this));
        } else {
            componentType.validate(context);
        }
    }
}
